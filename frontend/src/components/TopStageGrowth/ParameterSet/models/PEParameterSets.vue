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
                :value="beforeParameterSetData.fieldF"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">g</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.fieldG"
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
                :value="beforeParameterSetData.weibullA"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">β</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.weibullB"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">λ</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                :value="beforeParameterSetData.weibullL"
              ></v-text-field>
            </v-row>
          </v-col>
        </v-row>
      </v-container>
    </div>
    <div v-if="isEditMode && afterParameterSetData != null">
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
                v-model.trim.number="afterParameterSetData.fieldF"
                @change="onDisable"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">g</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.fieldG"
                @change="onDisable"
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
                v-model.trim.number="afterParameterSetData.weibullA"
                @change="onDisable"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">β</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.weibullB"
                @change="onDisable"
              ></v-text-field>
              <v-subheader class="ma-0 pa-1">λ</v-subheader>
              <v-text-field
                class="ma-0 pa-1"
                dense
                hide-details="auto"
                outlined
                v-model.trim.number="afterParameterSetData.weibullL"
                @change="onDisable"
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
  name: "ParameterSetName",
  props: {
    shared: Object,
  },
  mounted() {
    this.shared.mount(this);
  },
  data() {
    return {
      isEditMode: false,
      beforeParameterSetData: {},
      afterParameterSetData: null,
    };
  },
  methods: {
    //*----------------------------
    // 表示モードの設定
    //*----------------------------
    setEditMode(isEditMode) {
      this.isEditMode = isEditMode;
      if (this.isEditMode) {
        if (null == this.afterParameterSetData) {
          this.afterParameterSetData = this.beforeParameterSetData;
        }
      }
    },
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
            this.beforeParameterSetData = results.data;
            this.afterParameterSetData = results.data;
            this.shared.onConclude(this.beforeParameterSetData); // 上位クラスと共有
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
    addData() {
      usePhotosynthesisParamSetAdd(this.afterParameterSetData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("光合成量パラメータセットの追加に失敗しました。");
          } else {
            this.$emit("getItem", results.data);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //*----------------------------
    // 更新処理
    //*----------------------------
    putData() {
      usePhotosynthesisParamSetUpdate(this.afterParameterSetData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("光合成量パラメータセットの更新に失敗しました。");
          } else {
            this.$emit("getItem", results.data);
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