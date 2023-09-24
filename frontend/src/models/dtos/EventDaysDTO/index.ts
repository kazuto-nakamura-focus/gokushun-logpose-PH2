import FieldData from "@/models/element/FieldData";

type DTO = FieldData & {
  eventDaysList: Array<FieldData>
};

export default DTO;
