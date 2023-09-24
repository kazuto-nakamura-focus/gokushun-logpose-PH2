import FieldInfoDTO from "@/models/dtos/FieldInfoDTO";
import DeviceInfo from "@/models/dtos/DeviceInfoDTO";

type DTO = FieldInfoDTO & {
  deviceList: Array<DeviceInfo>;
};

export default DTO;
