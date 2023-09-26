<!--パラメーターセット表示-->
<!--パラメーターセット編集-->
<template>
  <v-container>
    <div>
      <div>
        <div class="text-subtitle-1" v-if="!isEditMode">パラメータ</div>
        <div class="text-subtitle-1" v-if="isEditMode">編集前パラメータ</div>
        <v-container class="sprout">
          <v-row>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽前</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.bd"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.be"
                ></v-text-field>
              </v-row>
            </v-col>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽後</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.ad"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeParameterSetData.ae"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>

      <div v-if="isEditMode && afterParameterSetData != null">
        <div class="text-subtitle-1">編集後パラメータ</div>
        <v-container class="sprout">
          <v-row>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽前</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.bd"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.be"
                ></v-text-field>
              </v-row>
            </v-col>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-0">萌芽後</v-subheader>
                <v-subheader class="ma-0 pa-1">d</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.ad"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.ae"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>
    </div>
  </v-container>
</template>

<script>
import {
  useGrowthParamSetDetail,
  useGrowthParamSetUpdate,
  useGrowthParamSetAdd,
} from "@/api/TopStateGrowth/GEParameterSets/index";

export default {
  props: {
    shared: Object,
  },
  data() {
    return {
      isEditMode: false,
      beforeParameterSetData: {},
      afterParameterSetData: null,
    };
  },
  mounted() {
    this.shared.mount(this);
  },
  methods: {
    //*----------------------------
    // 表示モードの設定
    //*----------------------------
    setEditMode(isEditMode) {
      this.isEditMode = isEditMode;
      if (this.isEditMode) {
        if (null == this.afterParameterSetData) {
          this.afterParameterSetData = Object.assign({},this.beforeParameterSetData);
        }
      }
    },
    //*----------------------------
    // 生育推定パラメータセット詳細取得
    //*----------------------------
    initialize(selectedId) {
      useGrowthParamSetDetail(selectedId)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("生育推定パラメータセット詳細取得に失敗しました。");
          } else {
            this.beforeParameterSetData = results.data;
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
    addData(name) {
      this.afterParameterSetData.parameterName = name;
      useGrowthParamSetAdd(this.afterParameterSetData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("生育推定パラメータセットの追加に失敗しました。");
          }else{
            alert("生育推定パラメータセットを追加しました。");
            // 追加されたデータを親と共有する
            this.shared.onConclude(this.afterParameterSetData);
          }
        }).bind(this)
        .catch((error) => {
          console.log(error);
        });
    },
    //*----------------------------
    // 更新処理
    //*----------------------------
    putData() {
      useGrowthParamSetUpdate(this.afterParameterSetData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("生育推定パラメータセットの更新に失敗しました。");
          }else{
            alert("生育推定パラメータセットを更新しました。");
            // 追加されたデータを親と共有する
            this.shared.onConclude(this.afterParameterSetData);
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
.sprout .v-input__control {
  width: 50px;
  padding: 0%;
}

.sprout .v-input__slot {
  padding: 10px;
}

.select_size {
  max-width: 40%;
  font-size: 14px;
}
</style>