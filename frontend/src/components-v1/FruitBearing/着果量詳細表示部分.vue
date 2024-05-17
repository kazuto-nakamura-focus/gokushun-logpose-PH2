<!--着果量着果負担表示画面-->
<template>
  <v-card>
    <v-data-table :headers="headers" :items="dataList" :item-class="itemClass"></v-data-table>
  </v-card>
</template>
  
  <script>
import { useFruitDetails } from "@/api/TopStateGrowth/index";

export default {
  data() {
    return {
      headers: [
        { text: "", value: "name" },
        { text: "実測日", value: "date" },
        { text: "収穫時樹冠葉面積(m^2)", value: "harvestCrownLeafArea" },
        {
          text: "積算樹冠光合成量(kgCO2vine^-1)",
          value: "culminatedCrownPhotoSynthesysAmount",
        },
        {
          text: "着果負担（果実総重量/収穫時樹冠葉面積）(g/m^2)",
          value: "bearingWeight",
        },
        {
          text: "積算樹冠光合成量あたりの着果量（果実総重量/積算樹冠光合成量）(g/kgCO2 vine^-1)",
          value: "bearingPerPhotoSynthesys",
        },
        {
          text: "実測着果数/収穫時樹冠葉面積(房数/m^2)",
          value: "bearingCount",
        },
      ],
      dataList: [],
    };
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
            // 20:25分 20:50
            // * 名前の検索
            let i = 0;
            for (const item of this.dataList) {
              if (item.name == name) {
                this.dataList.splice(i, 1);
                break;
              }
              i++;
            }
            // * 値の設定
            data.name = new String(name);
            if (data.date == null) {
              data.date = "なし";
            }
            if (data.bearingWeight == 0) {
              data.bearingWeight = "-";
            }
            if (data.bearingPerPhotoSynthesys == 0) {
              data.bearingPerPhotoSynthesys = "-";
            }
            if (data.bearingCount == 0) {
              data.bearingCount = "-";
            }
            if (this.dataList.length > 0) {
              data.keyDevice = new Boolean(false);
              this.dataList.splice(1, 0, data);
            } else {
              data.keyDevice = new Boolean(true);
              this.dataList.push(data);
            }
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
    itemClass(item) {
      return item.keyDevice == true ? "first-row" : "";
    },
  },
};
</script>
  
<style lang="css">
.first-row {
  background-color: #f4fce0;
  color: #000;
}
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
  