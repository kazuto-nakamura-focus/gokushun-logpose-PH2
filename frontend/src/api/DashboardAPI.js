import axios from "@/lib/axiosHooks";

// ダッシュボード対象デバイスの取得
const useDeviceTarget = () => {
  const config = {
    params: {},
  };
  return axios.get("/dashboard/devices", config);
};

// デバイスに対応するセンサー情報を取得
const useSensorList = (deviceId) => {
  const config = {
    params: { deviceId },
  };
  return axios.get("/dashboard/sensors", config);
};

//デバイス情報の更新
const useUpdateDisplay = (data) => {
  const config = {
    //   params: {},
    withCredential: true,
  };
  return axios.post("/dashboard/device", data, config);
};

//センサー情報の更新
const useUpdateSettings = (data) => {
  const config = {
    //   params: {},
    withCredential: true,
  };
  return axios.post("/dashboard/sensors", data, config);
};

export {
  useDeviceTarget,
  useSensorList,
  useUpdateDisplay,
  useUpdateSettings,
};
