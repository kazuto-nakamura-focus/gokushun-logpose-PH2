<!--着果量着果負担表示画面-->
<template>
  <v-container dense>
    <v-data-table :items="dataList" hide-default-header>
      <template v-slot:body="{ items }">
        <tbody>
          <tr v-bind:v-for="header in headers">
            <td>{{ header.text }}</td>
            <td v-bind:v-for="item in items">{{ item[header.value] }}</td>
          </tr>
        </tbody>
      </template>
    </v-data-table>
  </v-container>
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
                break;
              }
              i++;
            }
            if (null != this.target) {
              this.dataList.splice(i, 1);
            }
            // * 値の設定
            data.name = new String(name);
            this.dataList.unshift(data);
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
  },
};
</script>
  
  <style lang="scss" scoped>
@import "@/style/common.css";

.divider_top {
  margin-bottom: 10px;
  color: "black";
}

.divider_center {
  margin-top: 10px;
  margin-bottom: 10px;
  color: "black";
}

.divider_bottom {
  margin-top: 10px;
  color: "black";
}

.text_field_size {
  min-width: 60px;
}
</style>
  