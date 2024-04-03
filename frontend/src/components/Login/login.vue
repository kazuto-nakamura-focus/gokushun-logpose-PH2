<template>
  <v-container fill-height>
    <v-row justify="center" align-content="center">
      <!-- 画像を中央に配置 -->
      <v-col cols="12" sm="6" md="4">
        <v-img
          src="@/assets/gokushun-mark.webp"
          contain
          class="mx-auto mt-15"
        ></v-img>
        <!-- ログインボタンを中央に配置 -->
        <v-btn color="primary" block class="mt-5" @click="login">
          ログイン
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { axiosEnvConfig } from "@/config/envConfig";

const baseUrl = axiosEnvConfig.baseUrl;

export default {
  methods: {
    login() {
      // const redirectUrl = location.href;
      const redirectUrl = location.origin;
      const logoutUrl = `${baseUrl}/oauth2/authorization/heroku?redirect_uri=${redirectUrl}&mode=login`;

      window.location.href = logoutUrl;
    },
  },
};
</script>

<style>
/* 必要に応じて追加のスタイリング */
</style>
