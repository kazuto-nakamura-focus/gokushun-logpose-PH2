<template>
  <v-container>
    <v-row>
      <v-col cols="6">
        <sensor-menu ref="menu1"></sensor-menu>
        <sensor-menu ref="menu3"></sensor-menu>
        <sensor-menu ref="menu5"></sensor-menu>
        <sensor-menu ref="menu7"></sensor-menu>
        <sensor-menu ref="menu9"></sensor-menu>
      </v-col>

      <v-col cols="6">
        <sensor-menu ref="menu2"></sensor-menu>
        <sensor-menu ref="menu4"></sensor-menu>
        <sensor-menu ref="menu6"></sensor-menu>
        <sensor-menu ref="menu8"></sensor-menu>
        <sensor-menu ref="menu10"></sensor-menu>
      </v-col>
    </v-row>

    <div class="GS_ButtonArea">
      <v-btn
        color="primary"
        class="ma-2 white--text"
        elevation="2"
        @click="useUpdateSettings()"
        >更新する</v-btn
      >
    </div>
  </v-container>
</template>
<script>
import { useSensorList, useUpdateSettings } from "@/api/DashboardAPI.js";
import SensorMenu from "./SensorMenu.vue";
export default {
  props: {
    //* デバイスID
    deviceId: {
      type: Number,
      required: true,
    },
  },
  data: () => ({
    sensor: null,
    sensors: [],
    MAX: 10,
  }),
  components: {
    SensorMenu,
  },
  mounted() {
    // センサーリストの初期化
    this.setSensorMenu();
  },
  methods: {
    //* ============================================
    // APIのコールとデータの設定-センサー情報の取得
    //* ============================================
    setSensorMenu() {
      // * センサーデータの取得
      useSensorList(this.$options.propsData.deviceId)
        .then((response) => {
          const results = response["data"];
          //* 結果データの検証
          // エラー時
          if (results.status != 0) {
            throw new Error(results.message);
          } else {
            // メニューデータの作成
            this.createMenu(results.data.menu);
            // メニューコンポーネントへの設定
            this.setMenu(results.data.displays);
          }
        })
        .catch((error) => {
          console.log(error);
          alert("センサー情報の取得に失敗しました。");
        });
    },
    //* ============================================
    // APIのコールとデータの設定-センサー情報の更新
    //* ============================================
    useUpdateSettings: function () {
      // サーバーリクエストの送信データ作成
      const dashboardSet = this.createRequest();
      // APIのコール
      useUpdateSettings(dashboardSet)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            throw new Error(results.message);
          } else {
            alert("センサー情報の表示が更新されました。");
          }
        })
        .catch((error) => {
          console.log(error);
          alert("センサー情報の表示の更新に失敗しました。");
        });
    },
    //* ============================================
    // メニューの作成
    //* ============================================
    createMenu(serverData) {
      this.sensors = [];
      // 表示なし
      const notSelectSensor = {
        id: -1,
        name: "表示なし",
      };
      this.sensors.push(notSelectSensor);
      // センサーデータのメニューへの設定
      for (const item of serverData) {
        const menuItem = {
          id: item.id,
          name: item.name,
        };
        this.sensors.push(menuItem);
      }
      for (let i = 1; i <= this.MAX; i++) {
        this.$refs["menu" + i].setMenu(this.sensors);
        this.$refs["menu" + i].setDefault(-1);
      }
    },
    //* ============================================
    // メニューへの設定
    //* ============================================
    setMenu(serverData) {
      for (const item of serverData) {
        this.$refs["menu" + item.displayNo].setDefault(item.sensorId);
      }
    },
    //* ============================================
    // 更新リクエストの作成
    //* ============================================
    createRequest() {
      let args = [];
      for (let i = 1; i <= this.MAX; i++) {
        const selectedId = this.$refs["menu" + i].getSelected();
        const item = {
          displayNo: i,
          sensorId: selectedId == -1 ? null : selectedId,
        };
        args.push(item);
        console.log(item);
      }
      const dashboardSet = {
        deviceId: this.deviceId,
        sensors: args,
      };
      return dashboardSet;
    },
  },
};
</script>