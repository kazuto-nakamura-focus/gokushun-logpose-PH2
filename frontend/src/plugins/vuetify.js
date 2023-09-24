import Vue from "vue";
import Vuetify from "vuetify/lib/framework";
import "@mdi/font/css/materialdesignicons.css";

const theme = {
  themes: {
    light: {
      primary: "#00B428",
      // secondary: "#8bc34a",
      // accent: "#cddc39",
      // error: "#ffeb3b",
      // warning: "#ffc107",
      // info: "#ff5722",
      // success: "#795548",
    },
    dark: {
      primary: "#00B428",
      // secondary: "#8bc34a",
      // accent: "#cddc39",
      // error: "#ffeb3b",
      // warning: "#ffc107",
      // info: "#ff5722",
      // success: "#795548",
    },
  },
};

Vue.use(Vuetify);

export default new Vuetify({
  theme: theme,
  icons: {
    iconfont: "mdi",
  },
});
