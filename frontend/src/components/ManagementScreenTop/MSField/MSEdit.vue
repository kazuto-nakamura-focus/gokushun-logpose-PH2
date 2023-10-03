<template>
  <v-app>
    <v-container>
      <template>
        <v-row>
          <v-col cols="4">
            <v-text-field
              label="圃場名"
              :readonly="!isEditMode"
              dense
              hide-details="auto"
              outlined
              filled
              v-bind:background-color="bgcolor"
              v-model="fieldInfoData.name"
            ></v-text-field>
            <p></p>
          </v-col>
          <v-col cols="6" align="right">
            <div v-if="!isAddMode">
              <!--  更新モードボタン -->
              <v-icon right icon="mdi-vuetify" @click="changeMode()"
                >mdi-pencil-outline</v-icon
              >
              <v-icon right icon="mdi-vuetify" @click="deleteFieldInfo()"
                >mdi-trash-can-outline</v-icon
              >
            </div>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="6">
            <!-- google map -->
            <v-sheet
              id="map"
              v-bind:class="{
                'v-sheet--readonly': !isEditMode,
                'pa-0': isEditMode,
              }"
              :light="true"
              style="width: 100%; height: 400px"
            />

            <p></p>
            <v-row cols="3" justify="center">
              <v-col cols="3">
                <v-subheader>GPS Code</v-subheader>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  label="緯度（Latitude）"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  :readonly="!isEditMode"
                  v-model="mapHandler.latitude"
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  label="経度（Longitude）"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  :readonly="!isEditMode"
                  v-model="mapHandler.longitude"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-col>
          <v-col cols="1"></v-col>
          <v-col cols="5">
            <v-row>
              <v-col :cols="!isEditMode ? 12 : 9">
                <v-text-field
                  :readonly="!isEditMode"
                  v-bind:background-color="bgcolor"
                  label="場所"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  v-model="fieldInfoData.location"
                ></v-text-field>
              </v-col>
              <v-col v-if="isEditMode" cols="3">
                <v-btn
                  color="primary"
                  class="white--text"
                  elevation="2"
                  @click="changeFocusLatLng()"
                  >検索</v-btn
                >
              </v-col>
            </v-row>
            <p></p>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  :readonly="!isEditMode"
                  label="取引先"
                  v-bind:background-color="bgcolor"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  v-model="fieldInfoData.contructor"
                ></v-text-field>
              </v-col>
            </v-row>
            <p></p>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-bind:background-color="bgcolor"
                  label="登録日時"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  v-model="fieldInfoData.registeredDate"
                ></v-text-field>
              </v-col>
            </v-row>
            <p></p>
            <v-row v-if="!isAddMode">
              <v-col cols="11">
                <v-subheader>登録デバイス</v-subheader>
                <v-data-table
                  v-model="selected"
                  item-key="name"
                  :headers="headers"
                  dense
                  class="elevation-1"
                  mobile-breakpoint="0"
                  :hide-default-footer="true"
                  :items="useSelectDeviceInfoDataList"
                  @click:row="clickRow"
                  :useFieldInfoDataList="useFieldInfoDataList"
                >
                </v-data-table>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
        <v-card-actions>
          <v-spacer></v-spacer>
          <div v-if="!isEditMode" class="GS_ButtonArea">
            <v-btn
              color="gray"
              class="ma-2 black--text"
              elevation="2"
              @click="back()"
              >キャンセル</v-btn
            >
          </div>
          <div v-if="isEditMode" class="GS_ButtonArea">
            <v-btn
              v-show="!isAddMode"
              color="primary"
              class="ma-2 white--text"
              elevation="2"
              @click="update()"
              >更新</v-btn
            >
            <v-btn
              v-show="isAddMode"
              color="primary"
              class="ma-2 white--text"
              elevation="2"
              @click="add()"
              >追加</v-btn
            >
            <v-btn
              color="gray"
              class="ma-2 black--text"
              elevation="2"
              @click="back()"
              >キャンセル</v-btn
            >
          </div>
        </v-card-actions>
      </template>
    </v-container>
  </v-app>
</template>

<script>
import { Loader } from "@googlemaps/js-api-loader";
import {
  useFieldInfoUpdate,
  useFieldInfoAdd,
  useFieldInfoRemove,
  useMapLocation,
} from "@/api/ManagementScreenTop/MSField";
import {
  useDeviceList,
  useDeviceInfo,
} from "@/api/ManagementScreenTop/MSDevice";
import moment from "moment";

import { mapLoaderOptions, mapOptions } from "./mapConfig";

const HEADERS = [
  { text: "デバイス名", value: "name", sortable: true },
  { text: "品種名", value: "brand", sortable: true },
];

class MapHandler {
  constructor() {
    this.longitude = 0;
    this.latitude = 0;
  }
  setData(data, thisArgs, callBack) {
    if (null == data.longitude || null == data.latitude) {
      useMapLocation(data.location)
        .then((response) => {
          const responseData = response["data"];
          //データが存在するかを確認後、処理
          if(responseData && responseData.length > 0){
            var coordinates = responseData[0]["geometry"]["coordinates"];
            data.longitude = coordinates[0];
            data.latitude = coordinates[1];
            data.location = responseData[0]["properties"]["title"];
            this.longitude = data.longitude;
            this.latitude = data.latitude;
            callBack.apply(thisArgs);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    } else {
      this.longitude = data.longitude;
      this.latitude = data.latitude;
      callBack.apply(thisArgs, data);
    }
  }
}
//* --------------------------------------------------
// 更新・追加オブジェクトの生成
//* --------------------------------------------------
var createFieldObject = function (soruce, mapHandler) {
  const nowDate = moment().format("YYYY/MM/DD hh:mm:ss");
  soruce.latitude = parseFloat(mapHandler.latitude);
  soruce.longitude = parseFloat(mapHandler.longitude);
  //POSTやPUT例
  var id = (null == soruce.id)?null:parseInt(soruce.id);
  const data = {
    id: id,
    name: soruce.name,
    location: soruce.location,
    latitude: parseFloat(mapHandler.latitude),
    longitude: parseFloat(mapHandler.longitude),
    contructor: soruce.constructor,
    registeredDate: nowDate,
  };
  return data;
};

export default {
  props: {
    onUpdate: {
      type: Function,
      required: false,
    },
    onBackPressed: {
      type: Function,
      required: false,
    },
    onDelete: {
      type: Function,
      required: false,
    },
    onDeviceEdit: {
      type: Function,
      required: false,
    },
    useFieldInfoDataList: Array, //圃場一覧
    useFieldInfoData: Object, //圃場詳細
  },

  data() {
    return {
      selected: [],
      isEditMode: false,
      isAddMode: false,
      headers: HEADERS,

      useDeviceInfoData: null,
      fieldInfoSrcData: this.useFieldInfoData, // オリジナル
      fieldInfoEditData: this.useFieldInfoData, // 編集
      fieldInfoData: this.useFieldInfoData, // 表示

      bgcolor: "#FFF",
      useDeviceInfoDataList: [],
      useSelectDeviceInfoDataList: [],

      mapHandler: new MapHandler(),

      latitude: "",
      longtitude: "",
      splitArr: [],
      marker: null,
      address: "",
      
      mapLoader: null,
      map: null,
    };
  },

  mounted() {
    // 圃場にIDが無い場合、追加モードとする
    if (null == this.useFieldInfoData.id) {
      this.isAddMode = true;
      this.isEditMode = true;
    }

    //* 緯度経度の設定
    this.mapHandler.setData(this.useFieldInfoData, this, this.setMap);

    useDeviceList()
      .then((response) => {
        //成功時
        const { status, message, data } = response["data"];
        if (status === 0) {
          this.useDeviceInfoDataList = data;
          //圃場名が一致するデバイス一覧のみ格納
          this.useDeviceInfoDataList.map((item) => {
            if (item.field == this.useFieldInfoData.name) {
              this.useSelectDeviceInfoDataList.push(item);
            }
          });
        } else {
          throw new Error(message);
        }
      })
      .catch((error) => {
        //失敗時
        console.log(error);
      });
  },

  components: {},

  methods: {
    //* -----------------------------------------------
    // 地図の初期化
    //* -----------------------------------------------
    setMap() {
      if(this.mapLoader == null) this.mapLoader = new Loader(mapLoaderOptions);
      this.mapLoader.load()
        .then((google) => {
          if(this.google == null) this.google = google;

          // 地図の初期化
          this.map = new google.maps.Map(document.getElementById("map"), {
            // 初期表示設定
            ...mapOptions,
            //フォーカスを充てる座標を指定
            center: { lat: this.mapHandler.latitude, lng: this.mapHandler.longitude, }, 
            click: (event) => {
              console.log(event);
            }
          });
          
          if(this.marker){
            this.marker.setMap(null); 
          }
          this.marker = new google.maps.Marker({
            position:{lat:this.mapHandler.latitude, lng: this.mapHandler.longitude,},
            map: this.map,
            title: "位置情報"
          });
        })
        .catch((e) => {
          console.error(e);
        });
    },
    //* -----------------------------------------------
    // 更新モード・非更新モード変更時のフラグの設定及びデータの設定
    //* -----------------------------------------------
    changeMode() {
      //* 編集モードの時
      let isEditMode = !this.isEditMode;
      if (isEditMode) {
        this.fieldInfoSrcData = Object.assign({}, this.fieldInfoData);
        this.fieldInfoData.name = this.fieldInfoEditData.name;
        this.fieldInfoData.location = this.fieldInfoEditData.location;
        this.fieldInfoData.contructor = this.fieldInfoEditData.contructor;
        this.fieldInfoData.registeredDate =
          this.fieldInfoEditData.registeredDate;
        this.bgcolor = "#F4FCE0";
      } else {
        this.fieldInfoEditData = Object.assign({}, this.fieldInfoData);
        this.fieldInfoData.name = this.fieldInfoSrcData.name;
        this.fieldInfoData.location = this.fieldInfoSrcData.location;
        this.fieldInfoData.contructor = this.fieldInfoSrcData.contructor;
        this.fieldInfoData.registeredDate =
          this.fieldInfoSrcData.registeredDate;
        this.bgcolor = "#FFF";
      }
      this.isEditMode = isEditMode;
    },
    handleMapClick(event) {
      if (this.marker) {
        this.marker.setMap(null);
      }
      const latLng = event.latLng;
      const latitude = latLng.lat();
      const longitude = latLng.lng();
      this.marker = new this.google.maps.Marker({
        position: { lat: latitude, lng: longitude },
        map: this.map,
      });
      this.getAddressFromLatLng({ lat: latitude, lng: longitude });
    },

    getAddressFromLatLng(latLng) {
      const geocoder = new this.google.maps.Geocoder();
      geocoder.geocode({ location: latLng }, (results, status) => {
        if (status === "OK") {
          if (results[0]) {
            this.address = this.parseAddress(results[0].address_components);
            console.log("getAddressFromLatLng_address", this.address);
            this.fieldInfoData.location =
              this.address.city +
              ", " +
              this.address.prefecture +
              ", " +
              this.address.country;
          } else {
            console.log("No results found");
          }
        } else {
          console.log("Geocoder failed due to: " + status);
        }
      });
    },

    parseAddress(addressComponents) {
      let city = "";
      let prefecture = "";
      let country = "";
      for (let component of addressComponents) {
        if (component.types.includes("locality")) {
          city = component.long_name;
        }
        if (component.types.includes("administrative_area_level_1")) {
          prefecture = component.long_name;
        }
        if (component.types.includes("country")) {
          country = component.long_name;
        }
      }
      let parseAddressData = {
        city,
        prefecture,
        country,
      };
      return parseAddressData;
    },

    clickRow: function (item) {
      const clickRowData = item;

      //デバイス情報詳細取得(API)
      useDeviceInfo(clickRowData.id)
        .then(async (response) => {
          //成功時
          const { status, message, data } = response["data"];

          if (status === 0) {
            this.$emit("handleDeviceDetailInfo", data);
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },

    update: function () {
      if (confirm("更新してもよろしいですか？")) {
        // 更新オブジェクトの作成
        const data = createFieldObject(this.fieldInfoData, this.mapHandler);

        console.log("update_data", data);

        //圃場情報更新(API)
        useFieldInfoUpdate(data)
          .then((response) => {
            //成功時
            const { status, message } = response["data"];
            if (status === 0) {
              alert("更新を成功しました。");
              this.$emit("refreshFieldList");
              this.onUpdate();
            } else {
              throw new Error(message);
            }
          })
          .catch((error) => {
            //失敗時
            console.log(error);
            alert("更新を失敗しました。");
          });
      }
    },
    add: function () {
      if (confirm("追加してもよろしいですか？")) {
        // 更新オブジェクトの作成
        const data = createFieldObject(this.fieldInfoData, this.mapHandler);

        console.log("update_data", data);

        //圃場情報更新(API)
        useFieldInfoAdd(data)
          .then((response) => {
            //成功時
            const { status, message } = response["data"];
            if (status === 0) {
              alert("追加が成功しました。");
              this.$emit("refreshFieldList");
              this.onUpdate();
            } else {
              throw new Error(message);
            }
          })
          .catch((error) => {
            //失敗時
            console.log(error);
            alert("追加が失敗しました。");
          });
      }
    },
    back: function () {
      this.onBackPressed();
    },

    deleteFieldInfo: function () {
      if (confirm("削除してもよろしいですか？")) {
        console.log(
          "MSEdit_deleteFieldInfo_this.fieldInfoData.id",
          this.fieldInfoData.id
        );

        //圃場削除(API)
        useFieldInfoRemove(parseInt(this.fieldInfoData.id))
          .then((response) => {
            //成功時
            const { status, message } = response["data"];
            if (status === 0) {
              alert("削除に成功しました。");
              this.$emit("refreshFieldList");
              this.onDelete();
            } else {
              throw new Error(message);
            }
          })
          .catch((error) => {
            //失敗時
            console.log(error);
            alert("削除に失敗しました。");
          });
      }
    },

    changeFocusLatLng: function() {
      const { location } = this.fieldInfoData;
      if(!location) return;

      useMapLocation(location)
        .then((response) => {
          
          //場所検索後の座標を取得・格納
          const responseData = response["data"];
          //データが存在するかを確認後、処理
          if(responseData && responseData.length > 0){
          
            const coordinates = response.data[0]["geometry"]["coordinates"];
            const latitude = coordinates[1];
            const longitude = coordinates[0];
            
            const google = this.google;
            const map = this.map;

            //画面に描画している緯度・経度を更新する。
            const mapHandler = this.mapHandler;
            mapHandler["latitude"] = latitude;
            mapHandler["longitude"] = longitude;

            if(this.marker){
              this.marker.setMap(null); 
            }
            this.marker = new google.maps.Marker({
              position:{lat:latitude, lng: longitude},
              map: map,
              title: "位置情報"
            });

            const newCenter = new google.maps.LatLng(latitude, longitude);
            map.setCenter(newCenter);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });

    }
  },
};
</script>

<style lang="scss">
.v-sheet--readonly {
  pointer-events: none;
}
.readOnlyInput input {
  background-color: #ccc;
}
.editableInput input {
  background-color: #fff;
}
</style>
