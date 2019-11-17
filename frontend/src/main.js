import Vue from 'vue'
import App from './App.vue'
import Vuetify from 'vuetify'
import VueRouter from "vue-router";
import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify);
Vue.use(VueRouter)
Vue.config.productionTip = false;

const routes = [
  { path: '/', component:() => import("./components/Home.vue")},
  { path: '/visitors', component: () => import("./components/visitors/visitors.vue")},
  { path: '/downloader', component: () => import("./components/downloader/DownloaderQueue.vue")}
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
