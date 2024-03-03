export class HeaderClass {
    constructor() {
        this.size = 720;
        this.defaultHeaders = [
            {
                field: "order",
                headerName: "No.",
                suppressSizeToFit: true,
                resizable: true,
                width: 60,
            },
            {
                field: "stageName",
                headerName: "生育期名",
                resizable: true,
                width: 150,
            },
            {
                field: "elStage",
                headerName: "E-L Stage間隔",
                resizable: true,
                width: 138,
            },
            {
                field: "intervalF",
                headerName: "F値間隔",
                resizable: true,
                width: 100,
            },
            {
                field: "accumulatedF",
                headerName: "累積F値",
                resizable: true,
                width: 100,
            },
            {
                field: "color",
                headerName: "グラフ色",
                editable: false,
                resizable: false,
                width: 100,
            },
        ];
        this.EditableHeaders = [
            {
                field: "order",
                headerName: "No.",
                suppressSizeToFit: true,
                resizable: true,
                width: 55,
            },
            {
                field: "stageName",
                headerName: "生育期名",
                resizable: true,
                width: 110,
                editable: true,
            },
            {
                field: "elStage",
                headerName: "E-L Stage間隔",
                resizable: true,
                width: 90,
                editable: true,
            },
            {
                field: "intervalF",
                headerName: "F値間隔",
                resizable: true,
                width: 80,
                editable: true,
            },
            {
                field: "accumulatedF",
                headerName: "累積F値",
                resizable: true,
                width: 80,
                editable: true,
            },
            {
                field: "color",
                headerName: "グラフ色",
                editable: true,
                resizable: true,
                width: 90,
            },
            {
                field: "action",
                headerName: "",
                cellRenderer: "CellRender",
                colId: "action",
                editable: false,
                resizable: false,
                width: 180,
            },
        ];
    }
    getDefaultHeader = function () {
        return this.defaultHeaders;
    }
    getEditHeader = function () {
        return this.EditableHeaders;
    }

}