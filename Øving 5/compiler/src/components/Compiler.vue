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
import * as exec from 'browserify-exec'
import * as fs from 'browserify-fs'

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
    setInfo(string) {
      this.info = string;
    },
    submit() {
      this.setInfo("Kompilerer...");
      try {
        this.code = this.input
        if (this.code != undefined) {
          this.writeToFile()
          this.execCode()
        } else {
          this.error("Could not find the code input");
        }
      }catch (error) {
        this.setInfo("Noe gikk galt, sjekk loggen");
        this.output = error;
      }
      setTimeout(() => this.setInfo(""), 5000);
    },

    error(error) {
      throw new Error(JSON.stringify(error))
    },

    writeToFile() {
      fs.writeFile("books.txt", this.code, (err) => {
        throw new Error(err)
      })
    },

    execCode() {
      exec('docker build "./compile/" -q -t gcc', (buildLog, buildError) => {
        if (buildError) {
          this.error(buildError)
        }
        exec("docker run --rm gcc", (runOutput, runError) => {
          if (runError) {
            this.error(runError);
          }
          this.output = JSON.stringify({ result: `${buildLog}\n\n--- Output: ---\n\n${runOutput}` })
        })
      })
    },

  }
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
