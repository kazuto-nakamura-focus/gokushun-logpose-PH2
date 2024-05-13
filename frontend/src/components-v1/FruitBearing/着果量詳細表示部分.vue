<!--着果量着果負担表示画面-->
<template>
  <v-container dense>
    <v-data-table
      :headers="headers"
      :items="dataList"
      hide-default-header
    >
      <template v-slot:['item.${header}']="{ item }" v-for="header in headers">
        <tr>
          <td>{{ header.text }}</td>
          <td>{{ item[header.value] }}</td>
        </tr>
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
        {text :"", value : "name"},
        {text :"実測日", value : "date"},
        {text :"収穫時樹冠葉面積(m^2)", value : "harvestCrownLeafArea"},
        {text :"積算樹冠光合成量(kgCO2vine^-1)", value : "culminatedCrownPhotoSynthesysAmount"},
        {text :"着果負担（果実総重量/収穫時樹冠葉面積）(g/m^2)", value : "bearingWeight"},
        {text :"積算樹冠光合成量あたりの着果量（果実総重量/積算樹冠光合成量）(g/kgCO2 vine^-1)", value : "bearingPerPhotoSynthesys"},
        {text : "実測着果数/収穫時樹冠葉面積(房数/m^2)", value : "bearingCount"},
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
    let name = selectedItems.selectedField.name +
        "|" +
        selectedItems.selectedDevice.name +
        "|" +
        selectedItems.selectedYear.id;

    // * データの取得
        this.callUseFruitDetailsAPI(selectedItems.selectedDevice.id, selectedItems.selectedYear.id, name);
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
            for(const item of dataList){
                if(item.name == name) {
                    break;
                }
                i++;
            }
            if(null != target){
                dataList.splice(i, 1);
            }
            // * 値の設定
            data.name = new String(name);
            dataList.unshift(unshift);
          } else {
            throw new Error("データの取得ができませんでした。");
          }
        })
        .catch((e) => {
          alert(e.message);
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
  