<template>
  <v-app id="top">
    <!-- 認証状況を取得するまでは、Progressバーを表示する。 -->
    <v-container fill-height v-if="isLoading">
      <v-row justify="center" align-content="center">
        <v-col class="text-center">
          <v-progress-circular indeterminate size="70" width="7" color="primary" />
        </v-col>
      </v-row>
    </v-container>
    <!-- 認証状況の取得後に描画する画面 -->
    <div v-else>
      <v-navigation-drawer v-if="isLoggedIn" v-model="drawer" app>
        <navigation></navigation>
      </v-navigation-drawer>

      <v-app-bar v-if="isLoggedIn" app height="40">
        <v-app-bar-nav-icon color="white" @click="drawer = !drawer"></v-app-bar-nav-icon>
        <v-toolbar-title class="cm_TopTitle">極旬ログポース</v-toolbar-title>
      </v-app-bar>

      <v-main>
        <router-view></router-view>
      </v-main>
    </div>
  </v-app>
</template>

<script>
import { mapState } from "vuex";
import navigation from "@/components/parts/navigation.vue";
import store from "./store";
// import router from "./router"

export default {
  mounted() {
    document.addEventListener("visibilitychange", this.handleVisibilityChange);
  },
  beforeDestroy() {
    document.removeEventListener(
      "visibilitychange",
      this.handleVisibilityChange
    );
  },
  methods: {
    handleVisibilityChange() {
      const pathName = window.location.pathname;
      const loginUrl = `/login`;

      if (document.visibilityState === "visible") {
        // ブラウザが表示されたときに実行される処理
        store.dispatch("checkSession").then((isLoggedIn) => {
          console.log(isLoggedIn);
          console.log(pathName);
          if (!isLoggedIn) {
            if (!pathName.includes(loginUrl)) {
              alert(
                "ログインセッションがタイムアウトしました。再ログインしてください"
              );
              window.location.href = loginUrl;
            }
          } else {
            if (pathName.includes(loginUrl)) {
              window.location.href = "/";
            }
          }
        });
      }
    },
  },
  computed: {
    ...mapState({
      isLoading: (state) => state.isLoading,
      isLoggedIn: (state) => state.isLoggedIn,
    }),
  },
  components: { navigation },
  template: "<navigation>",
  data: () => ({ drawer: null }),
};
</script>
<style lang="scss">
@import "./src/sass/main.scss";
@import "./style/common.css";
@import "@mdi/font/css/materialdesignicons.css";
</style>
