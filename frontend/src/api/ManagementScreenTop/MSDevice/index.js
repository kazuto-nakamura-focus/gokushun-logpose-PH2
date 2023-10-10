import axios from "@/lib/axiosHooks";

//デバイス一覧取得
const useDeviceList = () => {
  const config = {
    params: {},
  };
  return axios.get("/device/list", config);
};

//デバイス情報追加
const useDeviceInfoAdd = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/device/info", data, config);
};

const useLoadData = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/device/sensorData", data, config);
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

export {
  useDeviceList,
  useDeviceInfoRemove,
  useDeviceInfo,
  useDeviceInfoAdd,
  useDeviceInfoUpdate,
  useDeviceMastersAPI,
  useLoadData,
};
