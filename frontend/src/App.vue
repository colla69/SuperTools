
  <template>
    <v-app id="app">
      <v-content >
      <div id="app">
        <Menu></Menu>
        <router-view></router-view>
      </div>
    </v-content>
  </v-app>

</template>


<script>

  import Menu from "./components/Menu";
  export default {
    name: 'app',
    components: {
      Menu,
    }
  }
</script>

<style>
  #app {
    font-family: 'Roboto', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    background: lightskyblue;
    color: #2c3e50;
    padding: 3px;
    margin: 0;
  }
</style>
