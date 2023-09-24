import FieldData from "@/models/element/FieldData";

type DTO = {
  id: number;
  field: string;
  device: string;
  dataList: Array<FieldData>;
};

export default DTO;
