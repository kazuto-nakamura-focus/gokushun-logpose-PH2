<!--実績値入力画面-->
<template>
    <v-card>
        <v-card-title>実績値入力</v-card-title>
        <v-container>
            <v-card-text>
                圃場名<br />
                <p class="font-weight-bold">{{ fieldName }} </p>
            </v-card-text>
            <v-card-text>
                現在の累積F値<br />
                <p class="font-weight-bold">{{ fValueInterval }}</p>
            </v-card-text>
            {{ selectedData.intervalF }}
            <div style="height: 250px">
                <div style="height: 250px; box-sizing: border-box">
                    <AgGridVue style="width: 100%; height: 100%" class="ag-theme-gs" sizeColumn :columnDefs="columnDefs"
                        :rowData="rowData" :onGridReady="onGridReady" :gridOptions="gridOptions"
                        :defaultColDef="defaultColDef" @cell-clicked="onCellClicked">
                    </AgGridVue>
                </div>
            </div>
            <!-- 実績値入力画面 -->
            <v-dialog v-model="pickerStatus" width="400" height="400">
                <v-date-picker v-model="picker"></v-date-picker>
                <v-btn @click="achievementValueDataGet">保存</v-btn>
            </v-dialog>
        </v-container>
        <div class="GS_ButtonArea">
            <v-btn color="primary" class="ma-2 white--text" elevation="2" @click="achievementValueDataSave()">保存</v-btn>
            <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="cancel()">キャンセル</v-btn>
        </div>
    </v-card>
</template>

<script>
import moment from "moment";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import { AgGridVue } from "ag-grid-vue";
import "@/style/ag-theme-gs.css";
import { useGrowthFData } from "@/api/TopStateGrowth/GEActualValueInput/index"

export default {
    name: "EditDialog",
    props: {
        selectedData: Array,
        isDialogEdit: Boolean,
    },
    components: {
        AgGridVue
    },
    data() {
        return {
            columnDefs: null,
            defaultColDef: null,
            param: null,
            params: [],
            columnTypes: null,
            gridOptions: {},
            gridApi: null,

            pickerStatus: false,
            picker: null,
            dataChangeStatus: null,

            fieldName: this.$store.getters.selectedField.name,
            deviceId: this.$store.getters.selectedDevice.id,
            year:this.$store.getters.selectedYear.id,
            date: moment().format("YYYY-MM-DD"),
            fValueInterval: 0,
            elStageIntervalFormatterStatus: false,
            originSelectData: [...this.selectedData],
            rowData: [],
            setOriginSelectedDataFValue: this.originSelectedDataFValue,
            achievementValueDataSaveData: [],
            saveIntervalF: 0,
            saveAccumulatedF: 0,
        }
    },
    beforeMount() {
        //ag-grid設定
        //F値変更カレンダー
        const elStageIntervalFormatter = (params) => {
            if (this.elStageIntervalFormatterStatus) {
                return `${params.value} -> ${this.fValueInterval}`;
            } else {
                return params.value
            }
        }
        //F値選択カレンダ
        const valueFormatter = (params) => {
            if (params.value || params.value == 0) {
                return params.value;
            } else {
                return '実績値入力';
            }
        }
        //累積F値選択カレンダ
        const elStageAccumulatedFormatter = (params) => {

            if (this.elStageIntervalFormatterStatus) {
                return this.saveAccumulatedF - (this.saveIntervalF - this.fValueInterval)
            } else {
                return params.value
            }
        }
        this.columnDefs = [
            { field: "order", headerName: "No.", suppressSizeToFit: true, resizable: true, width: 80 },
            { field: "stageName", headerName: "生育期名", resizable: true, width: 115 },
            { field: "elStage", headerName: "E-L Stage間隔", resizable: true, width: 115 },
            { field: "intervalF", headerName: "F値間隔", resizable: true, valueFormatter: elStageIntervalFormatter, width: 120 },
            { field: "accumulatedF", headerName: "累積F値", resizable: true, valueFormatter: elStageAccumulatedFormatter, width: 120 },
            { field: "targetDate", headerName: "実績", resizable: true, valueFormatter: valueFormatter, width: 120 }, //cc
        ];
        this.defaultColDef = {
            editable: false
        };
    },
    mounted() {
        this.gridApi = this.gridOptions.api;
        this.gridColumnApi = this.gridOptions.columnApi;
        //展開
        //現在の累積F値設定
        this.init()
    },
    updated() { },
    methods: {
        init: function () {
            let setData = this.selectedData[0]
            this.rowData = this.selectedData
            this.saveIntervalF = setData.intervalF
            this.saveAccumulatedF = setData.accumulatedF
            if (setData.targetDate) {
                //F値取得API
                this.getUseGrowthFData(setData.targetDate)
                this.picker = setData.targetDate
            } else {
                this.getUseGrowthFData(this.date)
            }
        },
        onGridReady: function () { },
        onCellClicked(params) {
            this.gridApi = params.api;
            this.gridColumnApi = params.columnApi;
            if (params.column.colId === 'targetDate') {
                this.pickerStatus = true
                this.dataChangeStatus = params.node.data.order
                // this.achievementValueDataSaveData.order = this.dataChangeStatus
            }
        },
        //カレンダー
        //選択日の実績値取得
        achievementValueDataGet() {
            if (this.picker) {
                //view F値、日付変更
                this.changeAchievement(this.getUseGrowthFData)
                this.elStageIntervalFormatterStatus = true
            } else {
                alert("日付を選択してください。")
            }
            this.pickerStatus = false
        },
        //実績値反映
        async changeAchievement(callback) {
            //選択日の実績値取得
            await callback(this.picker, this.deviceId);
            this.rowData[0].targetDate = this.picker
            this.rowData[0].accumulatedF = this.saveAccumulatedF - (this.saveIntervalF - this.fValueInterval)
            await this.gridApi.refreshCells({ force: true });
            this.rowData[0].intervalF = this.fValueInterval
            this.achievementValueDataSaveData = this.rowData[0]
        },
        //実績値保存
        achievementValueDataSave() {
            this.elStageIntervalFormatterStatus = false
            //DB保存API作成
            this.$emit("achievementValueDataSave", this.achievementValueDataSaveData)
        },
        cancel() {
            this.elStageIntervalFormatterStatus = false
            this.rowData = []
            this.picker = null
            this.fValueInterval = 0
            this.$emit('cancel')
        },
        async getUseGrowthFData(date) {
            //生育推定実績値取得
            await useGrowthFData(date, this.deviceId)
                .then((response) => {
                    const results = response["data"];
                    if(results.status==1){
                        alert(results.message);
                        this.fValueInterval = 0;
                    } else {
                        this.fValueInterval = results.data.value;
                    }
                })
                .catch((error) => {
                    //失敗時
                    console.log(error);
                });
        },
    },
    watch: {
        "isDialogEdit": function () {
            if (this.isDialogEdit) {
                this.init()
            }
        }

    }
};
</script>