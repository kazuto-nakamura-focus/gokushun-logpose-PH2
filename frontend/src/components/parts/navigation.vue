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
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-account</v-icon>
          </v-list-item-icon>
          <v-list-item-title>高橋史成</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-list>
  </div>
</template>

<script>
export default {
  //
};
</script>