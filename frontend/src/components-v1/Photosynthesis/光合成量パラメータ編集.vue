<template>
  <v-container>
    <div>
      <div class="text-subtitle-1" v-if="!isEditMode">パラメータ</div>
      <div class="text-subtitle-1" v-if="isEditMode">編集前パラメータ</div>
      <v-container class="photosynthesis">
        <v-row>
          <v-col cols="5">
            <v-row>
              <div class="text-subtitle-1">イールド値モデルパラメータ</div>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="6">
            <v-row>
              <div class="text-subtitle-1">ワイプル分布モデルパラメータ</div>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="5">
            <v-row>
              <v-subheader class="ma-0 pa-1">f</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.fieldF"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">g</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.fieldG"
              ></v-text-field>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="6">
            <v-row>
              <v-subheader class="ma-0 pa-1">α</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.weibullA"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">β</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.weibullB"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">λ</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeData.weibullL"
              ></v-text-field>
            </v-row>
          </v-col>
        </v-row>
      </v-container>
    </div>
    <div v-if="isEditMode && afterData != null">
      <div class="text-subtitle-1">編集後</div>
      <v-container class="photosynthesis">
        <v-row>
          <v-col cols="5">
            <v-row>
              <div class="text-subtitle-1">イールド値モデルパラメータ</div>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="6">
            <v-row>
              <div class="text-subtitle-1">ワイプル分布モデルパラメータ</div>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="5">
            <v-row>
              <v-subheader class="ma-0 pa-1">f</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.fieldF"
                background-color="#F4FCE0"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">g</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.fieldG"
                background-color="#F4FCE0"
              ></v-text-field>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="6">
            <v-row>
              <v-subheader class="ma-0 pa-1">α</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.weibullA"
                background-color="#F4FCE0"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">β</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.weibullB"
                background-color="#F4FCE0"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">λ</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterData.weibullL"
                background-color="#F4FCE0"
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
  usePhotosynthesisParamSetDetail,
  usePhotosynthesisParamSetUpdate,
  usePhotosynthesisParamSetAdd,
} from "@/api/TopStateGrowth/PEParameterSets/index";

export default {
  props: {
    isEditMode: Boolean,
    beforeParameterSetData: Object,
    afterParameterSetData: Object,
  },

  data() {
    return {
      beforeData: this.beforeParameterSetData,
      afterData: this.afterParameterSetData,
    };
  },
  methods: {
    //*----------------------------
    // 光合成量パラメータセット詳細取得
    //*----------------------------
    initialize(selectedId) {
      usePhotosynthesisParamSetDetail(selectedId)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("光合成量パラメータセット詳細取得に失敗しました。");
          } else {
            this.beforeData = results.data;
            this.afterData = Object.assign({}, this.beforeData);
            this.$emit("updateData", results.data);
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
      parentData.fieldF = this.afterData.fieldF;
      parentData.fieldG = this.afterData.fieldG;
      parentData.weibullA = this.afterData.weibullA;
      parentData.weibullB = this.afterData.weibullB;
      parentData.weibullL = this.afterData.weibullL;
      usePhotosynthesisParamSetAdd(parentData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("光合成量パラメータセットの追加に失敗しました。");
          } else {
            alert("光合成量パラメータセットを追加しました。");
            this.$emit("addData", parentData);
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
      parentData.fieldF = this.afterData.fieldF;
      parentData.fieldG = this.afterData.fieldG;
      parentData.weibullA = this.afterData.weibullA;
      parentData.weibullB = this.afterData.weibullB;
      parentData.weibullL = this.afterData.weibullL;
      usePhotosynthesisParamSetUpdate(parentData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("光合成量パラメータセットの更新に失敗しました。");
          } else {
            alert("光合成量パラメータセットを更新しました。");
            this.$emit("updateData", parentData);
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
.photosynthesis .v-input__control {
  width: 50px;
  padding: 0%;
}

.photosynthesis .v-input__slot {
  padding: 10px;
}
</style>