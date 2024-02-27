<template>
  <v-app>
    <v-container style="text-align: center">
      <v-card elevation="3" width="100%">
        <div class="cm_Title">現在稼働中の {{ title }} 一覧</div>
        <div style="font-size: 10pt">
          項目を変更する時は上のパネルの表示項目を選んでクリックしてください。
        </div>
        <v-data-table
          :headers="headers"
          :items="selectedDataList"
          mobile-breakpoint="0"
          :sort-by.sync="sortBy"
          :sort-desc.sync="sortDesc"
        >
          <template v-slot:[`item.url`]="{ item }">
            <v-img :src="item.url" width="30px"></v-img>
          </template>
        </v-data-table>
      </v-card>
    </v-container>
  </v-app>
</template>
<script>
import { useSum } from "@/api/Top";
export default {
  props: { shared /** MountController */: { required: true } },
  data() {
    return {
      title: "",
      selectedDataList: [],
      sortBy: "value",
      sortDesc: false,
      headers: [
        { text: "圃場", value: "name", sortable: true, width: "30%" },
        {
          text: "センサー名",
          value: "sensorName",
          sortable: true,
          width: "20%",
        },
        { text: "値", value: "value", sortable: true, width: "10%" },
        { text: "測定日", value: "date", sortable: true, width: "30%" },
        { text: "天気", value: "url", sortable: false, width: "10%" },
      ],
      headerMap: {
        1: "温度(℃)",
        2: "湿度(％RH)",
        3: "日射(W/㎡)",
        4: "樹液流(g/h)",
        5: "デンドロ(μm)",
        6: "葉面濡れ(raw counts)",
        7: "土壌水分(pF)",
        8: "土壌温度(℃)",
        9: "体積含水率-オーガニック(％)",
        10: "体積含水率-ミネラル(％)",
      },
    };
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    initialize: function (koumoku) {
      this.selectedDataList = [];
      this.title = this.headerMap[koumoku.variable];
      useSum(koumoku.variable)
        .then((response) => {
          //成功時
          const results = response["data"];
          for (const item of results.data) {
            var n = 2; // 小数点第n位まで残す
            item.value =
              Math.floor(item.value * Math.pow(10, n)) / Math.pow(10, n);
            var selected = {
              name: item.name,
              sensorName: item.sensorName,
              value: item.value,
              date: item.castedAt.substring(0, 19),
              url: "http:" + item.url,
            };
            this.selectedDataList.push(selected);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
  },
  created: function () {
    this.sourceData = this.$store.getters.sourceData;
  },
};
</script>
<style scoped>
</style>