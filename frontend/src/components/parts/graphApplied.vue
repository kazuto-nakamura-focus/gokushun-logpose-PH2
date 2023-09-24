<template>
  <div>
    <v-card elevation="2">
      <div class="dailogTitlePanel">
        <!-- 選ばれたモデルルート-->
        {{ title }}
      </div>
      <div style="text-align: left; font-size: 75%;margin:5px 0;">
        <!-- フィールド-->
        <span v-for="item in fields" :key="item.title">
          <label
            ><input
              type="checkbox"
              v-model="item.state"
              value="item"
              v-bind:checked="item.state==true"
              @change="change()"
              
            /><span>{{ item.title }}</span></label
          >&nbsp;
        </span>
      </div>
      <!-- 日付 -->
      <DateInput ref="di" @onClick="setData" />
      <v-divider></v-divider>
      <!-- グラフ -->
      <div style="width: 100%">
        <Graph ref="gr" :shared="sharedGraph"></Graph>
      </div>
    </v-card>
  </div>
</template>
<script>
import Graph from "@/components/parts/graph.vue";
import { MountController } from "@/lib/mountController.js";
import masterGrouthData from "@/assets/test/masterGrouthData.json";
import DateInput from "@/components/parts/dateInput.vue";


export default {
  props: { shared /** MountController */: { required: true } },

  data() {
    return {
      title: "",
      fields: [],
      dates: [],
      model: "",
      sharedGraph: new MountController(),

      selectedFields: [],

      masterGrouthData: masterGrouthData,
    };
  },

  components: {
    Graph, // グラフエリア
    DateInput
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    //* ----------------------------------------------
    // 初期化実行
    //* ----------------------------------------------
    initialize: function (title, sourceData, dates, model) {
      this.title = title;
      this.fields = sourceData;
      this.model = model;
      this.selectedFields = [];
      for (const item of sourceData) {
        if (item.state) {
          this.selectedFields.push(item);
        }
      }
      this.setData(dates);
      this.$refs.di.isDisabled = false;
    },

    change:function(){
      this.setData(this.dates);
    },

    setData: function (dates) {
      // 送信データ
      var dataList = [];
      var key = this.title;
      // 選択された各フィールドに対して
      for (const item of this.fields) {
         if(!item.state) continue;
        // マスターデータから一致するデータを取得
        var list = this.masterGrouthData[this.title];
        for (const master of list) {
          if (item.title == master.name) {
            key = key + "_" + item.title;
            var grData = { label: "", borderColor: "", data: [] };
            grData.label = item.title;
            grData.borderColor = master.borderColor;
            grData.data = master.data;
            grData.fill = false;
            grData.type = "line";
            grData.lineTension = 0.3;
            dataList.push(grData);
            break;
          }
        }
      }
      this.dates = dates;
      if(this.$refs.gr.initialize != undefined){
        this.$refs.gr.initialize(this.model, null, dataList);
      } else {
        this.sharedGraph.setUp(
          this.$refs.gr,
          function (gr) {
            gr.initialize(this.model, null, dataList);
          }.bind(this),
          function () {}.bind(this)
        );
      }
    },
  },
};
</script>