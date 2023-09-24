import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from "./router.js"
import store from './store'
import ToggleButton from 'vue-js-toggle-button'
import VuejsDialog from 'vuejs-dialog'
import DatePicker from 'vue2-datepicker'
import 'vue2-datepicker/index.css'

Vue.config.productionTip = false

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