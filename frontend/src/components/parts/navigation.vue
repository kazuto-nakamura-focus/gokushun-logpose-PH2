<template>
  <div>
    <v-list>
      <v-list nav dense>
        <v-list-item link to="/">
          <v-list-item-icon>
            <v-icon icon="mdi-vuetify">mdi-arrow-left-top-bold</v-icon>
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3"
            >Top画面に戻る</v-list-item-title
          >
        </v-list-item>

        <v-list-item link to="/top_graphs">
          <v-list-item-icon>
            <img src="@/assets/graph.png" />
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3"
            >グラフ</v-list-item-title
          >
        </v-list-item>
        <v-list-item link to="/top_data">
          <v-list-item-icon>
            <img src="@/assets/rawdata.png" />
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3"
            >生データ</v-list-item-title
          >
        </v-list-item>

        <v-list-item link to="/top_growth">
          <v-list-item-icon>
            <img src="@/assets/growth.png" />
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3"
            >成長予測(モデル式編集)</v-list-item-title
          >
        </v-list-item>

        <v-list-item link to="/management_screen_top">
          <v-list-item-icon>
            <img src="@/assets/manage.png" />
          </v-list-item-icon>
          <v-list-item-title class="green--text green--darken-3"
            >管理（管理者のみ）</v-list-item-title
          >
        </v-list-item>
        <v-list-item link v-if="userName != null">
          <v-list-item-icon>
            <v-icon>mdi-account</v-icon>
          </v-list-item-icon>
          <v-list-item-title width="100%" :title="userName">{{
            userName
          }}</v-list-item-title>
        </v-list-item>
        <v-list-item>
          <div style="display: flex; justify-content: right">
            <div>
              <!-- <button v-if="userName == null" v-on:click="login">
                <u>ログイン</u>
              </button> -->
              <button v-if="userName != null" v-on:click="logout">
                <u>ログアウト</u>
              </button>
            </div>
          </div>
        </v-list-item>
      </v-list>
    </v-list>
  </div>
</template>

<script>
// import { watchEffect } from "vue";
import { axiosEnvConfig } from "@/config/envConfig";
// import { AuthCookies } from "@/lib/AuthCookies.js";
import { useGetUser } from "@/api/Auth/auth";

const baseUrl = axiosEnvConfig.baseUrl;

export default {
  data() {
    return {
      userName: null,
    };
  },
  mounted() {
    useGetUser()
      .then((response) => {
        //成功時
        const results = response["data"].data;
        return results;
      })
      .then((user) => {
        if (user == null) throw Error("Not exists user");
        
        let name = user["name"];
        if(name == null){
          name = user["email"];
        }
        this.userName = name;
      })
      .catch((error) => {
        //失敗時
        console.log(error);
      });
  },
  methods: {
    //ログインボタン押下時、バックエンド側にログイン処理用URLにリダイレクトする。
    login: function () {
      const redirectUrl = location.href;
      const logoutUrl = `${baseUrl}/oauth2/authorization/heroku?redirect_uri=${redirectUrl}&mode=login`;

      window.location.href = logoutUrl;
    },
    //ログアウトボタン押下時、バックエンド側にログアウト処理用URLにリダイレクトする。
    logout: function () {
      const redirectUrl = location.href;
      const logoutUrl = `${baseUrl}/oauth2/authorization/heroku?redirect_uri=${redirectUrl}&mode=unlink`;

      window.location.href = logoutUrl;
    },
  },
};
</script>
