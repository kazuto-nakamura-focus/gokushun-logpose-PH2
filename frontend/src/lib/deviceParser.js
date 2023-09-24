export class DeviceParser {
  constructor() { }
  parse(order, data) {
    var returnList = [];
    var targets = [];
    for (const orderItem of order.labels) {
      var item = {};
      item.title = orderItem.variable;
      item.count = 0;
      item.state = false;
      targets.push(item);
    }
    var count = 0;
    for (const dataElem of data) {
      if (dataElem.state) {
        count++;
        for (item of dataElem.items) {
          if(item.variable.length > 0) {
            for (const target of targets) {
              if(target.title == item.variable ){
                target.count++;
                break;
              }
            }
          }
        }
      }
    }
    for (const target of targets){
      if(target.count == count){
        returnList.push(target);
      }
    }
    return returnList;
  }
}

