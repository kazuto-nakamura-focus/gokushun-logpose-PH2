<template>
  <div>
    <v-list>
      <v-list nav dense>
        <v-list-item link to="/">
          <v-list-item-icon>
            <v-icon icon="mdi-vuetify">mdi-arrow-left-top-bold</v-icon>
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3">Top画面に戻る</v-list-item-title>
        </v-list-item>

        <v-list-item link to="/top_graphs">
          <v-list-item-icon>
            <img src="@/assets/graph.png" />
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3">グラフ</v-list-item-title>
        </v-list-item>
        <v-list-item link to="/top_data">
          <v-list-item-icon>
            <img src="@/assets/rawdata.png" />
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3">生データ</v-list-item-title>
        </v-list-item>

        <v-list-item link to="/top_growth">
          <v-list-item-icon>
            <img src="@/assets/growth.png" />
          </v-list-item-icon>
          <v-list-item-title class="gray--text gray--darken-3">成長予測(モデル式編集)</v-list-item-title>
        </v-list-item>

        <v-list-item link to="/management_screen_top">
          <v-list-item-icon>
            <img src="@/assets/manage.png" />
          </v-list-item-icon>
          <v-list-item-title class="green--text green--darken-3">管理（管理者のみ）</v-list-item-title>
        </v-list-item>
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-account</v-icon>
          </v-list-item-icon>
          <v-list-item-title width="100%" :title="userName">{{userName}}</v-list-item-title>
        </v-list-item>
        <v-list-item>
          <div style="display: flex;justify-content: right;">
            <div>
              <button v-on:click="logout">
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
import { AuthCookies } from "@/lib/AuthCookies.js";
import { useLogout } from "@/api/authAPI";

export default {
  data() {
    return {
      userName: "",
    };
  },
  mounted() {
    let cookies = new AuthCookies();
    this.userName = cookies.get("name");
    if (null == this.userName) {
      this.setName();
    }
  },
  methods: {
    logout: function () {
      useLogout()
        .then((response) => {
          console.log(response);
        })
        .catch((error) => {
          console.log(error);
        })
        .finally(() => {});
    },
    setName: function () {
      this.intervalid1 = setInterval(
        function () {
          let cookies = new AuthCookies();
          this.userName = cookies.get("name");
          if (null != this.userName) {
            console.log("name " + this.userName);
            clearInterval(this.intervalid1);
          }
        }.bind(this),
        1000
      );
    },
  },
};
</script>