<template>
  <v-container>
    <div>
      <div class="text-subtitle-1" v-if="!isEditMode">パラメータ</div>
      <div class="text-subtitle-1" v-if="isEditMode">編集前パラメータ</div>

      <v-container class="leafArea">
        <v-row>
          <v-col cols="6">
            <v-row>
              <div class="text-subtitle-1">樹冠葉面積モデルパラメータ</div>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <div class="text-subtitle-1">葉枚数モデルパラメータ</div>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="6">
            <v-row>
              <v-subheader class="ma-0 pa-1">a</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.areaA"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">b</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.areaB"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.areaC"
              ></v-text-field>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.countC"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">d</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.countD"
              ></v-text-field>
            </v-row>
          </v-col>
        </v-row>
      </v-container>
    </div>

    <div v-if="isEditMode && afterData != null">
      <div class="text-subtitle-1">編集後</div>
      <v-container class="leafArea">
        <v-row>
          <v-col cols="6">
            <v-row>
              <div class="text-subtitle-1">樹冠葉面積モデルパラメータ</div>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <div class="text-subtitle-1">葉枚数モデルパラメータ</div>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="6">
            <v-row>
              <v-subheader class="ma-0 pa-1">a</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.areaA"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">b</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.areaB"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.areaC"
              ></v-text-field>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <v-subheader class="ma-0 pa-1">c</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.countC"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">d</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.countD"
              ></v-text-field>
            </v-row>
          </v-col>
        </v-row>
      </v-container>
    </div>
  </v-container>
</template>

<script>
import {
  useLeafParamSetDetail,
  useLeafParamSetUpdate,
  useLeafParamSetAdd,
} from "@/api/TopStateGrowth/LAParameterSets/index";

export default {
  props: {
    isEditMode:Boolean,
    beforeParameterSetData: Object,
    afterParameterSetData:Object,
  },

  data() {
    return {
      beforeData:this.beforeParameterSetData,
      afterData:this.afterParameterSetData
    };
  },
  methods: {
    //*----------------------------
    // 葉面積パラメータセット詳細取得
    //*----------------------------
    initialize(selectedId) {
      useLeafParamSetDetail(selectedId)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("葉面積パラメータセット詳細取得に失敗しました。");
          } else {
            this.beforeData = results.data;
            this.afterData = Object.assign({}, this.beforeData);
            this.$emit('updateData',results.data );
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //*----------------------------
    // 追加処理
    //*----------------------------
    addData(parentData) {
      parentData.areaA = this.afterData.areaA;
      parentData.areaB = this.afterData.areaB;
      parentData.areaC = this.afterData.areaC;
      parentData.countC = this.afterData.countC;
      parentData.countD = this.afterData.countD;
      useLeafParamSetAdd(parentData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("葉面積パラメータセットの追加に失敗しました。");
          }else {
            alert("葉面積パラメータセットを追加しました。");
            this.$emit('addData',results.data );
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //*----------------------------
    // 更新処理
    //*----------------------------
    updateData(parentData) {
      parentData.areaA = this.afterData.areaA;
      parentData.areaB = this.afterData.areaB;
      parentData.areaC = this.afterData.areaC;
      parentData.countC = this.afterData.countC;
      parentData.countD = this.afterData.countD;
      useLeafParamSetUpdate(parentData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("葉面積パラメータセットの更新に失敗しました。");
          } else{
            alert("葉面積パラメータセットを更新しました。");
            this.$emit('updateData',parentData );
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
  },
};
</script>

<style>
.leafArea .v-input__control {
  width: 50px;
  padding: 0%;
}

.leafArea .v-input__slot {
  padding: 10px;
}
</style>