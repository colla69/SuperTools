import Vue from 'vue'
import App from './App.vue'
import Vuetify from 'vuetify'
import VueRouter from "vue-router";
import 'vuetify/dist/vuetify.min.css'
import Home from "./components/Home";
import Visitors from "./components/visitors/visitors"
import DownloaderQueue from "./components/downloader/DownloaderQueue";

Vue.use(Vuetify);
Vue.use(VueRouter)
Vue.config.productionTip = false;

const routes = [
  { path: '/', component: Home },
  { path: '/visitors', component: Visitors },
  { path: '/downloader', component: DownloaderQueue }
];

const router = new VueRouter({
  routes: routes
});

new Vue({
  data: {
    currentRoute: window.location.pathname
  },
  router: router,
  render: h => h(App)
  //render (h) { return h(this.ViewComponent) }
  }).$mount('#app');
