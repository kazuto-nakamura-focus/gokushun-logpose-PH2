import Label from "@/models/element/Label";
import FieldData from "@/models/element/FieldData";

type DTO = Label & {
  device: string;
  fieldList: Array<FieldData>;
};

export default DTO;
