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
                  :value="beforeData.bd"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeData.be"
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
                  :value="beforeData.ad"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="beforeData.ae"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>

      <div v-if="isEditMode && afterData != null">
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
                  v-model.trim.number="afterData.bd"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterData.be"
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
                  v-model.trim.number="afterData.ad"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">e</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterData.ae"
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
            this.beforeData = results.data;
            this.afterData = Object.assign({}, this.beforeData);
          this.$emit("updateData",results.data );
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
      parentData.bd = this.afterData.bd;
      parentData.be = this.afterData.be;
      parentData.ad = this.afterData.ad;
      parentData.ae = this.afterData.ae;
      useGrowthParamSetAdd(parentData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("生育推定パラメータセットの追加に失敗しました。");
          }else{
            alert("生育推定パラメータセットを追加しました。");
           this.$emit('addData',results.data );
          }
        }).bind(this)
        .catch((error) => {
          console.log(error);
        });
    },
    //*----------------------------
    // 更新処理
    //*----------------------------
    updateData(parentData) {
      parentData.bd = this.afterData.bd;
      parentData.be = this.afterData.be;
      parentData.ad = this.afterData.ad;
      parentData.ae = this.afterData.ae;
      useGrowthParamSetUpdate(parentData)
        .then((response) => {
          //成功時
          const results = response["data"];
          if (results.status != 0) {
            console.log(results.message);
            alert("生育推定パラメータセットの更新に失敗しました。");
          }else{
            alert("生育推定パラメータセットを更新しました。");
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