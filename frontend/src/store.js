// store.js
import Vue from "vue";
import Vuex from "vuex";
import { useSessionCheck } from "./api/Auth/auth";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    isLoggedIn: false,
    isLoading: true,
  },
  mutations: {
    setLoggedIn(state, status) {
      state.isLoggedIn = status;
    },
    setLoading(state, status) {
      state.isLoading = status;
    },
  },
  actions: {
    checkSession({ commit }) {
      return new Promise((resolve, reject) => {
        useSessionCheck()
          .then((response) => {
            const isLoggedIn = response["data"];
            console.log("isLoggedIn", isLoggedIn);
            commit("setLoggedIn", isLoggedIn);
            commit("setLoading", false);
            resolve(isLoggedIn);
          })
          .catch((error) => {
            console.log(error);
            commit("setLoggedIn", false);
            commit("setLoading", false);
            reject(error);
          })
      });
    },
  },
});
