# Roadmap การเรียนรู้ Java + Spring Boot สำหรับเตรียมสอบสัมภาษณ์ 🚀

โปรเจคท์ **shortenurlservice-java** เป็นตัวอย่างที่ดีมากในการเรียนรู้ เพราะมีองค์ประกอบที่บริษัทเทคโนโลยีมักจะถามในการสัมภาษณ์งาน (โดยเฉพาะตำแหน่ง Backend Engineer)

---

## 1. พื้นฐานที่ควรรู้ (Fundamentals)
ในโปรเจคท์นี้ ให้ลองสังเกตสิ่งเหล่านี้:

*   **Annotation ของ Spring Boot:**
    *   `@SpringBootApplication`: จุดเริ่มต้นของแอป
    *   `@RestController`: การสร้าง API แบบ REST
    *   `@Service`: เลเยอร์ Business Logic
    *   `@Repository`: เลเยอร์ที่ติดต่อกับ Database
    *   `@Value`: การดึงค่าจาก `.env` หรือ `application.properties`
*   **Dependency Injection (DI):** สังเกตการใช้ Constructor Injection ใน `UrlShortenerService` (ทำไมต้องใช้? ดีกว่า `@Autowired` ตรงไหน?)
*   **Maven (pom.xml):** การจัดการ library ต่างๆ และ build lifecycle

## 2. การทำงานกับข้อมูล (Data & Persistence)
เนื่องจากโปรเจคท์นี้ใช้ **MongoDB** และ **Redis**:

*   **NoSQL vs SQL:** ทำไมโปรเจคท์นี้ถึงเลือกใช้ MongoDB? (ลองหาคำตอบเผื่อโดนถามเรื่องความแตกต่าง)
*   **Caching Pattern:** ดูใน `UrlShortenerService.java` จะเห็นรูปแบบ **Look-aside Cache**:
    1. เช็คใน Redis ก่อน
    2. ถ้าไม่มี ค่อยไปหาใน MongoDB
    3. เจอแล้วเอามาใส่ Redis ไว้
    *   *คำถามสัมภาษณ์:* Cache Invalidation คืออะไร? เราตั้งเวลา TTL (7 วันในโค้ด) เพื่ออะไร?
*   **Lombok:** การใช้ `@Data`, `@Getter`, `@Setter` เพื่อลด Boilerplate code

## 3. System Design (หัวใจสำคัญของ URL Shortener)
โจทย์การทำ URL Shortener เป็นโจทย์คลาสสิกในการสัมภาษณ์ System Design:

*   **Hashing/Encoding:** เราใช้ `ShortCodeGenerator` แบบไหน? (Base62 vs Base64?)
*   **Collision Handling:** ถ้าสุ่มแล้วได้รหัสซ้ำจะทำยังไง? (แอปนี้ใช้ `do-while` loop เพื่อ retry)
*   **Scalability:** ถ้ามีคนใช้พันล้านคนต่อวัน MongoDB ตัวเดียวจะไหวไหม? เราจะทำ Sharding ยังไง?
*   **Availability:** ถ้า Redis ล่ม แอปยังทำงานได้ไหม?

## 4. หัวข้อที่แนะนำให้ลองทำเพิ่ม (Next Steps)
หากต้องการเก่งขึ้นและโชว์ผลงานในตอนสัมภาษณ์ ให้ลองเพิ่มสิ่งเหล่านี้:

1.  **Unit Testing:** เขียนเทสโดยใช้ JUnit 5 และ Mockito (หัวใจสำคัญของ Senior Dev)
2.  **Validation:** เพิ่มการเช็คว่า URL ที่ส่งมาถูกต้องไหม (Regex/UrlValidator)
3.  **Analytics:** เพิ่มการเก็บสถิติว่าแต่ละรหัสมีคนคลิกกี่ครั้ง (Click counter)
4.  **Dockerization:** ลองรันแอปผ่าน Docker Compose (มีไฟล์ให้แล้ว ลองแกะดูว่ามันเชื่อมต่อกันยังไง)

---

## 📝 บันทึก Q&A จากการเรียนรู้

### Q1: Constructor Injection ดีกว่า Field Injection (@Autowired) อย่างไร?
**A:** Constructor Injection เป็นที่นิยมที่สุดเพราะ:
1.  **Immutability:** รองรับการใช้ `final` field (Field injection ทำไม่ได้)
2.  **Testability:** เขียน Unit Test ง่าย ไม่ต้องรัน Spring Container (ใช้ `new Service(mock)`)
3.  **Required Dependencies:** บังคับให้ส่ง dependency ตั้งแต่สร้าง Object ลดความเสี่ยง NULL
4.  **Circular Dependency:** Spring จะดักเจอได้ทันทีตอน startup

### Q2: Spring Boot จัดการ Life cycle (Scopes) เหมือน .NET Core ไหม?
**A:** คล้ายกันมากครับ:
*   `AddSingleton` (dotnet) = **Singleton** (Spring default)
*   `AddTransient` (dotnet) = **Prototype** (Spring) - `@Scope("prototype")`
*   `AddScoped` (dotnet) = **Request** (Spring) - `@RequestScope`

---

### จุดที่ควรโฟกัสในโค้ดปัจจุบัน:
1.  [UrlShortenerController.java](src/main/java/com/warota/shorturlservice/controller/UrlShortenerController.java): ดูวิธีการรับข้อมูลและการทำ Redirect (HTTP 301 vs 302 ต่างกันยังไง?)
2.  [UrlShortenerService.java](src/main/java/com/warota/shorturlservice/service/UrlShortenerService.java): ดู Logic การสลับระหว่าง Cache และ DB
3.  [ShortUrlEntry.java](src/main/java/com/warota/shorturlservice/model/ShortUrlEntry.java): ดูการ map object เข้ากับ MongoDB document
