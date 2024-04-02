import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from "./router.js"
import store from './store'
import ToggleButton from 'vue-js-toggle-button'
import VuejsDialog from 'vuejs-dialog'
import DatePicker from 'vue2-datepicker'
import 'vue2-datepicker/index.css'
import { AuthCookies } from "@/lib/AuthCookies.js";

Vue.config.productionTip = false

let cookies = new AuthCookies();
if (null == cookies.get("at")) {
  window.location.replace("/login.html");
}
else {
  new Vue({
    router,
    vuetify,
    options: {
      customProperties: true
    },
    store,
    render: h => h(App)
  }).$mount('#app')

  Vue.use(ToggleButton);
  Vue.use(VuejsDialog);
  Vue.use(DatePicker);
}
