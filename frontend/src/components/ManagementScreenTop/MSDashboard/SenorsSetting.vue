<template>
  <v-container>
    <v-row>
      <v-col cols="6">
        <sensor-menu ref="menu1"></sensor-menu>

        <sensor-menu ref="menu2"></sensor-menu>

        <sensor-menu ref="menu3"></sensor-menu>

        <sensor-menu ref="menu4"></sensor-menu>
      </v-col>

      <v-col cols="6">
        <sensor-menu ref="menu5"></sensor-menu>

        <sensor-menu ref="menu6"></sensor-menu>

        <sensor-menu ref="menu7"></sensor-menu>

        <sensor-menu ref="menu8"></sensor-menu>
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
    MAX: 8,
  }),
  components: {
    SensorMenu,
  },
  mounted() {
    this.callSensorListAPI();
  },
  methods: {
    //* ============================================
    // APIのコールとデータの設定-センサー情報の取得
    //* ============================================
    callSensorListAPI() {
      useSensorList(this.$options.propsData.deviceId)
        .then((response) => {
          const results = response["data"];
          //* 結果データの検証
          if (results.status != 0) {
            console.log(results.message);
            alert("センサー情報の取得に失敗しました。");
          } else {
            //* メニュ―項目の設定
            const dashBoardSensorsDTOList = results.data;
            //* 全メニューの設定 --- START
            let hasSetting = false;
            for (const item of dashBoardSensorsDTOList) {
              const menuItem = {
                id: item.sensorId,
                name: item.sensorName,
              };
              this.sensors.push(menuItem);
              if (item.displayNo != null) hasSetting = true;
            }
            for (let i = 1; i <= this.MAX; i++) {
              this.$refs["menu" + i].setMenu(this.sensors);
            }
            //* --- END
            //* 既に設置済み場合
            if (hasSetting) {
              for (const item of dashBoardSensorsDTOList) {
                if (item.displayNo != null) {
                  this.$refs["menu" + item.displayNo].setDefault(item.sensorId);
                }
              }
            } else {
              for (const item of dashBoardSensorsDTOList) {
                this.$refs["menu" + item.dataId].setDefault(item.sensorId);
              }
            }
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //* ============================================
    // APIのコールとデータの設定-センサー情報の更新
    //* ============================================
    useUpdateSettings: function () {
      let args = [];
      for (let i = 1; i <= this.MAX; i++) {
        const item = {
          displayNo: i,
          sensorId: this.$refs["menu" + i].getSelected(),
        };
        args.push(item);
      }
      const dashboardSet = {
        deviceId: this.deviceId,
        sensors: args,
      };
      useUpdateSettings(dashboardSet)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("センサー情報の表示の更新に失敗しました。");
          } else {
            alert("センサー情報の表示が更新されました。");
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
  },
};
</script>