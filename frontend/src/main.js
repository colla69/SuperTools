import Vue from 'vue'
import App from './App.vue'

import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import '@mdi/font/css/materialdesignicons.css'
import VueRouter from "vue-router";
import VueCompositionApi from '@vue/composition-api'

Vue.use(Vuetify);

const vuetify = new Vuetify({
  icons: {
    iconfont: 'mdi'
  }
});

Vue.use(VueRouter);
Vue.config.productionTip = false;
const routes = [
  { path: '/', component:() => import("./components/Home.vue")},
  { path: '/visitors', component: () => import("./components/visitors/visitors.vue")},
  { path: '/mangas', component: () => import("./components/mangas/mangaList.vue")},
  { path: '/downloader', component: () => import("./components/downloader/DownloaderQueue.vue")}
];
const router = new VueRouter({
  routes: routes
});

Vue.use(VueCompositionApi);

new Vue({
  data: {
    currentRoute: window.location.pathname
  },
  router: router,
  render: h => h(App),
  vuetify
  //render (h) { return h(this.ViewComponent) }
  }).$mount('#app');
