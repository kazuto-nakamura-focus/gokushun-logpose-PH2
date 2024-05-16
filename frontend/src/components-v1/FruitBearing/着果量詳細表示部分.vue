<!--着果量着果負担表示画面-->
<template>
  <v-card>
    <ph-2-bearling-data ref="ph2BearlingData" @mounted="ph2BearlingDataMounted"></ph-2-bearling-data>
  </v-card>
</template>
  
  <script>
import { useFruitDetails } from "@/api/TopStateGrowth/index";
import ph2BearlingData from "@/components-v1/FruitBearing/着果量データタブ.vue";

export default {
  data() {
    return {
      name: null,
      lastData: null,
    };
  },
  components: {
    ph2BearlingData,
  },
  mounted() {
    this.$emit("mounted");
  },
  methods: {
    initialize: function (selectedItems) {
      // * 表示名
      let name =
        selectedItems.selectedField.name +
        "|" +
        selectedItems.selectedDevice.name +
        "|" +
        selectedItems.selectedYear.id;

      // * データの取得
      this.callUseFruitDetailsAPI(
        selectedItems.selectedDevice.id,
        selectedItems.selectedYear.id,
        name
      );
    },
    //* ============================================
    // 着果量負担の詳細を取得する
    //* ============================================
    callUseFruitDetailsAPI(deviceId, year, name) {
      useFruitDetails(deviceId, year)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status === 0) {
            this.lastData = data;
            this.name = name;
            this.ph2BearlingDataMounted();
          } else {
            throw new Error(message);
          }
        })
        .catch((e) => {
          alert("データの取得ができませんでした。");
          //失敗時
          console.log(e);
        });
    },
    ph2BearlingDataMounted() {
      if (this.name != null && this.$refs.ph2BearlingData !== undefined) {
        this.$nextTick(function () {
          this.$refs.ph2BearlingData.addData(this.name, this.lastData);
        });
      }
    },
  },
};
</script>
  
<style scoped>
.wrapper {
  display: -webkit-flex;
  display: -moz-flex;
  display: -ms-flex;
  display: -o-flex;
  display: flex;
  flex-wrap: wrap;
}
.ph2_data_title {
  width: 100%;
  font-size: 9pt;
  border-bottom: 1px solid #ccc;
  padding: 3px 0;
}
</style>
  