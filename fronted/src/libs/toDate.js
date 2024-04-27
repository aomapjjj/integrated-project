export function toDate(dateTimeString){
    
        // แยกวันที่และเวลา
        const [datePart, timePart] = dateTimeString.split("T");
        const date = datePart.split("-");
        const time = timePart.replace("Z", "").split(":")
      
        // แปลงแต่ละส่วนเป็นรูปแบบที่ต้องการโดยใช้ map()
        const formattedDate = date.map(part => parseInt(part))
        const formattedTime = time.map(part => parseInt(part))
      
        const mixed =  formattedDate.concat(formattedTime).join(", ")
        const event = new Date(Date.UTC(mixed))
    
        return event.toLocaleString('en-GB', { timeZone: 'UTC' })
    
      
}