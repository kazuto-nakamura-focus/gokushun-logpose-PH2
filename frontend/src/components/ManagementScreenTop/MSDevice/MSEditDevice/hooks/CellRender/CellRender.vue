<template>
  <v-select :items="items" dense v-model="selectName"></v-select>
</template>

<script>
import SensorData from "@/assets/testData/SensorList.json";
import MSEditDevice from "@/assets/testData/MSEditDevice.json";

export default {
  props: {
    rowDataArray: Object,
  },
  name: "CellRender",
  data: () => ({
    items: [],
    selectName: null,
  }),

  mounted() {
    //センサー種類のマスターデータ習得APIが必要
    this.items = SensorData; //testData

    // this.selectName = this.rowDataArray.name

    // this.SensorName = MSEditDevice//testData

    // for (var i = 0; i < this.SensorName.length; i++) {

    //     console.log("selectd[i]", this.SensorName[i].name);
    //     this.selectName = this.SensorName[i].name
    // }
  },
};
</script>


 