<template>
  <v-app id="top">
    <!-- 認証状況を取得するまでは、Progressバーを表示する。 -->
    <v-container fill-height v-if="isLoading">
      <v-row justify="center" align-content="center">
        <v-col class="text-center">
          <v-progress-circular
            indeterminate
            size="70"
            width="7"
            color="primary"
          />
        </v-col>
      </v-row>
    </v-container>
    <!-- 認証状況の取得後に描画する画面 -->
    <div v-else>
      <v-navigation-drawer v-if="isLoggedIn" v-model="drawer" app>
        <navigation></navigation>
      </v-navigation-drawer>

      <v-app-bar v-if="isLoggedIn" app height="40">
        <v-app-bar-nav-icon
          color="white"
          @click="drawer = !drawer"
        ></v-app-bar-nav-icon>
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

export default {
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
