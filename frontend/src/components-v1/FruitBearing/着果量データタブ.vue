<template>
  <v-container v-if="dataList.length > 0">
    <v-row>
      <v-col cols="3">
        <div class="data_title"></div>
      </v-col>
      <v-col v-for="item in dataList" cols="2" :key="item.id">
        <div class="data_Value" style="font-size:9pt">{{item.name}}</div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider class="divider_top" />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <div class="data_title">最終実測日</div>
      </v-col>
      <v-col v-for="item in dataList" cols="2" :key="item.id">
        <div class="data_Value" style="font-size:11pt;text-align:right">{{item.date}}</div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider class="divider_top" />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <div class="data_title">収穫時樹冠葉面積(m^2)</div>
      </v-col>
      <v-col v-for="item in dataList" cols="2" :key="item.id">
        <div
          class="data_Value"
          style="font-size:11pt;text-align:right"
        >{{item.harvestCrownLeafArea}}</div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider class="divider_top" />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <div class="data_title">
          積算樹冠光合成量
          <br />(kgCO2vine^-1)
        </div>
      </v-col>
      <v-col v-for="item in dataList" cols="2" :key="item.id">
        <div
          class="data_Value"
          style="font-size:11pt;text-align:right"
        >{{item.culminatedCrownPhotoSynthesysAmount}}</div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider class="divider_top" />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <div class="data_title">
          着果負担（果実総重量/収穫時樹冠葉面積）
          <br />(g/m^2)
        </div>
      </v-col>
      <v-col v-for="item in dataList" cols="2" :key="item.id">
        <div class="data_Value" style="font-size:11pt;text-align:right">{{item.bearingWeight}}</div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider class="divider_top" />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <div class="data_title">
          積算樹冠光合成量あたりの着果量（果実総重量/積算樹冠光合成量）
          <br />(g/kgCO2 vine^-1)
        </div>
      </v-col>
      <v-col v-for="item in dataList" cols="2" :key="item.id">
        <div
          class="data_Value"
          style="font-size:11pt;text-align:right"
        >{{item.bearingPerPhotoSynthesys}}</div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider class="divider_top" />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <div class="data_title">
          実測着果数/収穫時樹冠葉面積
          <br />(房数/m^2)
        </div>
      </v-col>
      <v-col v-for="item in dataList" cols="2" :key="item.id">
        <div class="data_Value" style="font-size:11pt;text-align:right">{{item.bearingCount}}</div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider class="divider_top" />
      </v-col>
    </v-row>
  </v-container>
</template>
<script>
export default {
  //* ============================================
  // データ
  //* ============================================
  data() {
    return {
      dataList: [], // タブに表示するITEM
      id: 0,
      tab: null,
    };
  },
  mounted() {
    this.$emit("mounted");
  },
  methods: {
    //* ============================================
    // グラフ追加処理
    //* ============================================
    addData: function (title, data) {
      let i = 0;
      for (const item of this.dataList) {
        if (item.name == title) {
          this.dataList.splice(i, 1);
          break;
        }
        i++;
      }
      // * 値の設定
      data.id = new Number(this.id);
      data.name = new String(title);
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
      this.dataList.unshift(data);
      if (this.dataList.length > 4) {
        this.dataList.splice(4, 1);
      }
      this.tab = this.dataList.length - 1;
      this.id++;
      console.log(this.dataList);
    },
    //* ============================================
    // グラフアイテムの削除
    //* ============================================
    deleteItem: function (id) {
      this.dataList.forEach((item, index) => {
        if (item.id == id) {
          this.dataList.splice(index, 1);
        }
      });
      if (this.dataList.length > 0) {
        this.tab = this.dataList.length - 1;
      }
    },
  },
};
</script>
<style scoped>
.data_title {
  font-size: 9pt;
  color: #999;
}
</style>