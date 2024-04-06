import Vue from 'vue'
import Vuex from 'vuex'
import { useSessionCheck } from '@/api/Auth/auth';

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    sourceData: null,
    selectedField:null,
    selectedDevice:null,
    selectedYear:null,
    selectedData: {
      selectedMenu: null,
      selectedFields: [],
      selectedDevices: [],
      selectedYears: [],
    },
    // this.$store.state.login.userId の形で呼び出し
    login: {
      userId: 2,
      userName: null,
    },
    
    isLoggedIn: false,
    isLoading: true,
  },
  getters: {
    selectedField: state => state.selectedField,
    selectedDevice:state => state.selectedDevice,
    selectedYear:state => state.selectedYear,
    sourceData: state => state.sourceData,
    selectedData: state => state.selectedData,
  },
  mutations: {
    setSelectedField: (state, selectedField) => state.selectedField = selectedField,
    setSelectedDevice: (state, selectedDevice) => state.selectedDevice = selectedDevice,
    setSelectedYear: (state, selectedYear) => state.selectedYear = selectedYear,

    setSourceData: (state, sourceData) =>
      state.sourceData = sourceData,
    setSelectedModel: (state, modelData) => {
      state.selectedData.selectedMenu = modelData
    },
    setSelectedFields: (state, sourceFields) => {
      if (sourceFields.mode) {
        state.selectedData.selectedFields.push(sourceFields.data)
      }
      else {
        state.selectedData.selectedFields.splice(state.selectedData.selectedFields.indexOf(sourceFields.data), 1);
      }
    },
    setSelectedDevices: (state, sourceDevices) => {
      if (sourceDevices.mode) {
        state.selectedData.selectedDevices.push(sourceDevices.data)
      }
      else {
        state.selectedData.selectedDevices.splice(state.selectedData.selectedDevices.indexOf(sourceDevices.data), 1);
      }
    },
    setSelectedYears: (state, sourceYears) => {
      if (sourceYears.mode) {
        state.selectedData.selectedYears.push(sourceYears.data)
      }
      else {
        state.selectedData.selectedYears.splice(state.selectedData.selectedYears.indexOf(sourceYears.data), 1);
      }
    },
    // ログイン状態変更
    setLoggedIn(state, status) {
      state.isLoggedIn = status;
    },
    // セッション読込状態変更
    setLoading(state, status) {
      state.isLoading = status;
    },
  },
  actions: {
    //セッションチェック
    //セッションチェック用APIを利用して、ステータス設定する。
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
    changeSelectedField({ commit }, val) {
      commit("setSelectedField", val);
    },
    changeSelectedDevice({ commit }, val) {
      commit("setSelectedDevice", val);
    },
    changeSelectedYear({ commit }, val) {
      commit("setSelectedYear", val);
    },
    
    changeSourceData({ commit }, val) {
      commit("setSourceData", val);
    },
    changeModel({ commit }, val) {
      commit("setSelectedModel", val);
    },
    changeFields({ commit }, val) {
      commit("setSelectedFields", val);
    },
    changeDevices({ commit }, val) {
      commit("setSelectedDevices", val);
    },
    changeYears({ commit }, val) {
      commit("setSelectedYears", val);
    },
  },
  modules: {

  }
}
);
