<template>
  <div class="hello">
    <h1>C++ compile!</h1>
    <form class="content" @submit.prevent="submit">
        <div class="input">
            <label for="inputArea">Input code:</label>
            <textarea id="inputArea" width="100%" name="inputArea" rows="20" v-model="input"/>
        </div>
        <div class="input">
            <label for="inputArea">Output:</label>
            <textarea disabled id="outputArea" width="100%" v-model="output" rows="20"/>
        </div>
        <button type="submit" @click="submit">Compile</button>
        <p id="info">{{info}}</p>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      info: "",
      input: "#include <iostream> \nusing namespace std; \nint main(){\n\t{\n\tcout << \"Hello world\" << endl;\n\t}\n}",
      output: "",
      code: null,
    }
  },
  name: 'Compiler',
  methods:{
    submit() {
      this.code = this.input;

        axios({
          url: "http://localhost:8080/",
          method: "post",
          params: {
            code: this.code
          }
        })
        .then(response => {
          this.output = response.data;
        })
        .catch(error => console.error(error));
      }
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.content {
  margin: 10px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 10px;
}
h1 {
  font-size: 120px;
  padding: 10px;
}
.input {
  display: flex;
  flex-direction: column;
}
textarea {
  color: black;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid black;
  resize: none;
  font-family: monospace;
}
button {
  color: black;
  padding: 20px;
  border-radius: 5px;
  border: 1px solid black;
  cursor: pointer;
  font-size: 20px;
  font-weight: bold;
}
button:hover {
  background-color: cornsilk;
}
</style>
