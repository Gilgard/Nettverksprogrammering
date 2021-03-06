const http = require('http');
const crypto = require('crypto');
const static = require('node-static');

const port = 3210;

const file = new static.Server('./');
const server = http.createServer((req, res) => {
  req.addListener('end', () => file.serve(req, res)).resume();
});

server.on('upgrade', function (req, socket) {
  if (req.headers['upgrade'] !== 'websocket') {
    socket.end('HTTP/1.1 400 Bad Request');
    return;
  }

  // Read the websocket key provided by the client:
  const acceptKey = req.headers['sec-websocket-key'];
  // Generate the response value to use in the response:
  const hash = generateAcceptValue(acceptKey);
  // Write the HTTP response into an array of response lines:
  const responseHeaders = [ 'HTTP/1.1 101 Web Socket Protocol Handshake', 'Upgrade: WebSocket', 'Connection: Upgrade', `Sec-WebSocket-Accept: ${hash}` ];

  // Read the subprotocol from the client request headers:
  const protocol = req.headers['sec-websocket-protocol'];
  // If provided, they'll be formatted as a comma-delimited string of protocol
  // names that the client supports; we'll need to parse the header value, if
  // provided, and see what options the client is offering:
  const protocols = !protocol ? [] : protocol.split(',').map(s => s.trim());
  // To keep it simple, we'll just see if JSON was an option, and if so, include
  // it in the HTTP response:
  if (protocols.includes('json')) {
    console.log("Contains json")
    // Tell the client that we agree to communicate with JSON data
    responseHeaders.push(`Sec-WebSocket-Protocol: json`);
  }

  // Write the response back to the client socket
  socket.write(responseHeaders.join('\r\n') + '\r\n\r\n');

  socket.on('data', buffer => {
    console.log("Received data")
    const message = parseMessage(buffer);
    console.log(message)
    if (message) {
    // For our convenience, so we can see what the client sent
    console.log(message);
    // We'll just send a hardcoded message in this example
    socket.write(constructReply({ message: 'Hello from the server!' }));
    } else if (message === null) {
        console.log('WebSocket connection closed by the client.');
    }
  });

  socket.on('error', (error) => {
    console.error('Error: ', error);
  });

  socket.on('end', () => {
    console.log('Client disconnected');
  });
});

server.listen(port, () => console.log(`Server running at http://localhost:${port}`));

function constructReply(data) {
  // Convert the data to JSON and copy it into a buffer
  const json = JSON.stringify(data)
  const jsonByteLength = Buffer.byteLength(json);
  // Note: we're not supporting > 65535 byte payloads at this stage 
  const lengthByteCount = jsonByteLength < 126 ? 0 : 2; 
  const payloadLength = lengthByteCount === 0 ? jsonByteLength : 126; 
  const buffer = Buffer.alloc(2 + lengthByteCount + jsonByteLength); 
  // Write out the first byte, using opcode `1` to indicate that the message 
  // payload contains text data 
  buffer.writeUInt8(0b10000001, 0); 
  buffer.writeUInt8(payloadLength, 1); 
  // Write the length of the JSON payload to the second byte 
  let payloadOffset = 2; 
  if (lengthByteCount > 0) { 
    buffer.writeUInt16BE(jsonByteLength, 2); payloadOffset += lengthByteCount; 
  } 
  // Write the JSON data to the data buffer 
  buffer.write(json, payloadOffset); 
  return buffer;
}

function parseMessage(data) {
  let melding = "";
  let length = data[1] & 127;
  let maskStart = 2;
  let dataStart = maskStart + 4;
  for(let i = dataStart; i < dataStart + length; i++) {
      let byte = data[i] ^ data[maskStart + ((i - dataStart) % 4)];
      melding += String.fromCharCode(byte);
  }
  console.log(melding);
  return melding;
}

function generateAcceptValue (acceptKey) {
  return crypto
  .createHash('sha1')
  .update(acceptKey + '258EAFA5-E914-47DA-95CA-C5AB0DC85B11', 'binary')
  .digest('base64');
}