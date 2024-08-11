import axios from "@/lib/axiosHooks";

//デバイス一覧取得
const useDeviceList = () => {
  const config = {
    params: {},
  };
  return axios.get("/device/list", config);
};

//デバイス一覧取得
const useDeviceShortList = () => {
  const config = {
    params: {},
  };
  return axios.get("/device/list-short", config);
};

//デバイス情報追加
const useDeviceInfoAdd = (data) => {
  const config = {
    params: {},
  };

  return axios.post("/device/info", data, config);
};

//デバイス情報更新
const useDeviceInfoUpdate = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/device/info", data, config);
};

//デバイス情報詳細取得
const useDeviceInfo = (deviceId) => {
  const config = {
    params: { deviceId },
  };
  return axios.get("/device/info", config);
};

//デバイス削除
const useDeviceInfoRemove = (deviceId) => {
  const config = {
    params: { deviceId },
  };
  return axios.delete("/device/info", config);
};

//デバイスマスターの取得
const useDeviceMastersAPI = () => {
  const config = {
    params: {},
  };
  return axios.get("/device/masters", config);
};
// ======================================================
// デバイスのロードスケジュールを得る
// ======================================================
//ロード対象の取得
const useGetSchedule = () => {
  const config = {
    params: {},
  };
  return axios.get("/bulk/load/schedule", config);
};

// ======================================================
// デバイスのロード情報を得る
// ======================================================
const useGetAllDataLoadStatus = () => {
  const config = {
    params: {},
  };
  return axios.get("/bulk/load/info", config);
  /* 以下のリスト
    {
    private String name;
    private Long id;
    private String status;
    private String loadTime;
    private String updateTime;
  }
  */
};
// ======================================================
// センサーロードのログ情報を得る
// ======================================================
const useGetLog = (deviceId, type) => {
  const config = {
    params: { deviceId, type },
  };
  return axios.get("/bulk/load/log", config);
  /* 以下のリスト
    {
    private String date;
    private String message;
    }
  */
};
// ======================================================
// デバイス更新のリクエストを要求する
// ======================================================
const usePostRequest = (data) => {
  /*
    {
    deviceId : "デバイスID"
    }
  */
  const config = {
    params: {},
  };
  return axios.post("/bulk/load/device", data, config);
};


export {
  useDeviceList,
  useDeviceShortList,
  useDeviceInfoRemove,
  useDeviceInfo,
  useDeviceInfoAdd,
  useDeviceInfoUpdate,
  useDeviceMastersAPI,
  useGetSchedule,
  useGetAllDataLoadStatus,
  useGetLog,
  usePostRequest
};
