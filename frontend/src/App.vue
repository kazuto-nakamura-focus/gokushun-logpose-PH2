<template>
  <v-app id="top">
    <v-navigation-drawer v-model="drawer" app>
      <navigation></navigation>
    </v-navigation-drawer>

    <v-app-bar app height="40">
      <v-app-bar-nav-icon color="white" @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title class="cm_TopTitle">極旬ログポース</v-toolbar-title>
    </v-app-bar>

    <v-main>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script>
import navigation from "@/components/parts/navigation.vue";
//import Top from "./components/Top.vue";

export default {
  components: { navigation },
  template: "<navigation>",
  data: () => ({ drawer: null }),
  beforeRouteUpdate(to, from, next) {
    // URL の id が変わったときに src/components/items-detail.vue の updateItem 関数を実行してモデルを更新する
    this.$refs.items.updateItem();
    next();
  },
};
</script>
<style lang="scss">
@import "./src/sass/main.scss";
@import './style/common.css';
@import '@mdi/font/css/materialdesignicons.css';
</style>

