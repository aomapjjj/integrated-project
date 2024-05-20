export function toDate(dateTimeString) {
    if(dateTimeString === null){
        return null
    }
      const [datePart, timePart] = dateTimeString.split("T")
      const [year, month, day] = datePart?.split("-")
      
      let [hour, minute, second] = [0, 0, 0]
      if (timePart) {
          [hour, minute, second] = timePart?.replace("Z", "")?.split(":")
      }
      
      const event = new Date(Date.UTC(year, month - 1, day, hour, minute, second))
      
      const formattedDate = event?.toLocaleDateString('en-GB')
      const formattedTime = event?.toLocaleTimeString('en-GB')
  
      return `${formattedDate} ${formattedTime}`
  }
  
      
