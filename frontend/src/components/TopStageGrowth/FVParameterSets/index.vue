<!--着果量着果負担表示画面-->
<template>
  <v-container class="container_de">
    <div class="felx_de">
  <!--   <v-row class="row_wrap">
        <v-col cols="1" class="row_hd">
        </v-col>
        <v-col class="row_de" v-for="(data, i) in fVActualValueTitle" :key="i">
          <v-subheader>{{ data }}
          </v-subheader>
        </v-col>
      </v-row>-->
      <v-divider class="divider_top" />
      <v-row>
        <v-col cols="1" class="row_hd">
          <v-subheader>着果負担
          </v-subheader>
        </v-col>
        <v-col class="row_de" v-for="(data, i) in fruitValues" :key="i">
          <v-subheader>
            {{ data.burden }}&nbsp;
            <div v-if="i > 0" class="view_plus">
              <div v-if="fruitValues[i].burden - fruitValues[0].burden > 0">
                (+{{ fruitValues[i].burden - fruitValues[0].burden }})
              </div>
              <div v-else-if="fruitValues[i].burden - fruitValues[0].burden == 0">
              </div>
              <div v-else class="view_minus">
                ({{ fruitValues[i].burden - fruitValues[0].burden }})
              </div>
            </div>
          </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_center" />
      <v-row>
        <v-col cols="1" class="row_hd">
          <v-subheader>積算推定樹冠光合成量あたりの着果量（g/mol）
          </v-subheader>
        </v-col>
        <v-col class="row_de" v-for="(data, i) in fruitValues" :key="i">
          <v-subheader>
            {{ data.amount }}&nbsp;
            <div v-if="i > 0" class="view_plus">
              <div v-if="fruitValues[i].amount - fruitValues[0].amount > 0">
                (+{{ fruitValues[i].amount - fruitValues[0].amount }})
              </div>
              <div v-else-if="fruitValues[i].amount - fruitValues[0].amount == 0">
              </div>
              <div v-else class="view_minus">
                ({{ fruitValues[i].amount - fruitValues[0].amount }})
              </div>
            </div>
          </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_center" />
      <v-row>
        <v-col cols="1" class="row_hd">
          <v-subheader>実測着果数/樹冠葉面積（房数/㎠）
          </v-subheader>
        </v-col>
        <v-col class="row_de" v-for="(data, i) in fruitValues" :key="i">
          <v-subheader>
            {{ data.count }}&nbsp;
            <div v-if="i > 0" class="view_plus">
              <div v-if="fruitValues[i].count - fruitValues[0].count > 0">
                (+{{ fruitValues[i].count - fruitValues[0].count }})
              </div>
              <div v-else-if="fruitValues[i].count - fruitValues[0].count == 0">
              </div>
              <div v-else class="view_minus">
                ({{ fruitValues[i].count - fruitValues[0].count }})
              </div>
            </div>
          </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_bottom" />
    </div>
  </v-container>
</template>

<script>
import { useFruitValues } from "@/api/TopStateGrowth/index"

export default {
  props: {
    shared /** MountController */: { required: true },
  },
  components: {
  },
  data() {
    return {
      fields: this.$store.getters.selectedData.selectedFields, //選ばれた圃場たち
      devices: this.$store.getters.selectedData.selectedDevices, //選ばれた圃場たち
      year: this.$store.getters.selectedData.selectedYears[0].variable, //年度
      fVActualValueTitle: [], // 選ばれた圃場名
      fruitValues: [],
    };
  },
  mounted() {
    this.shared.mount(this);
    this.getUseFruitValues()
  },
  updated() {
  },
  methods: {
    async getUseFruitValues() {

      this.fVActualValueTitle = []
      this.fruitValues = []

      //選択した圃場名指定
      // const field = this.$store.getters.selectedData.selectedFields
      //this.fields = this.$store.getters.selectedData.selectedFields
      //this.fields = this.$store.getters.selectedData.selectedFields
      //選択した年度
      // const year = this.$store.getters.selectedData.selectedYears
      // this.year = this.selectedYears[0].variable

      console.log("updated", this.devices)
      console.log("updated", this.year)

      for (let i = 0; this.devices.length > i; i++) {
        //圃場着果量着果負担詳細取得
        await useFruitValues(this.devices[i].id, this.year).then((response) => {
          //成功時
          const results = response["data"];
          console.log(results);
          this.fVActualValueTitle.push(this.devices[i].name) //選択した圃場タイトル
          if (results.data) {
            this.fruitValues.push(results.data) //成功時
          } else {
            this.fruitValues.push({
              "amount": 0,
              "burden": 0,
              "count": 0
            }) //成功時
          }

          // this.fruitValues.push(fruitValuesDTOListData[i]) //削除mockデータ
        }).catch((error) => {
          //失敗時
          console.log(error);
          // this.fruitValues.push(fruitValuesDTOListData[i])
        })
      }
    },
    //圃場、年度変更し処理
    updateTable() {
      this.getUseFruitValues()
    }
  },
  watch: {
    "fields": function () {
      console.log("1111111111")
    },
    "year": function () {
      console.log("2222222222")
    },
  }
};
</script>

<style lang="scss" >
@import "@/style/common.css";

.theme--light.v-text-field--solo>.v-input__control>.v-input__slot {
  background: #f4f5fa;
}

.divider_top {
  margin-bottom: 25px;
  color: "black";
}

.divider_center {
  margin-top: 25px;
  margin-bottom: 25px;
  color: "black";
}

.divider_bottom {
  margin-top: 25px;
  color: "black";
}

.view_plus {
  color: red;
}

.view_minus {
  color: blue;
}

.row_hd {
  max-width: 400px;
  width: 400px;
}

.row_de {
  max-width: 300px;
}

.felx_de {
  scroll-snap-align: start;
}

.container_de {
  overflow: auto;
  display: grid;
  grid-template-columns: repeat(1, 260vw)
}
</style>
