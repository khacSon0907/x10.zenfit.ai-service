# Kiến trúc Maven Multi-Module và Hướng dẫn sử dụng POM (Parent & Child)

Tài liệu này giải thích chi tiết cơ chế hoạt động của hệ thống **Maven Multi-Module** trong dự án **Zenfit AI Service**, vai trò của file `pom.xml` cha (Parent POM) và các file `pom.xml` con (Child POM), cùng các quy tắc chuẩn khi khai báo và quản lý thư viện.

---

## 1. Tổng quan mô hình Maven Multi-Module

Dự án áp dụng mô hình **Multi-Module Project** theo tư tưởng Clean Architecture:
- Toàn bộ source code được chia thành các module nhỏ, độc lập về mặt nghiệp vụ hoặc hạ tầng (`commons`, `core-user-module`, `core-security-module`, `api-portal-service`, ...).
- Một **Parent POM** nằm ở thư mục gốc đóng vai trò điều phối toàn bộ dự án.
- Mỗi module con sở hữu một **Child POM** riêng để định nghĩa những gì nó cần.

```
x10.zenfit.ai-service (Root Directory)
├── pom.xml                                  <-- PARENT POM (Quản lý chung)
├── commons/
│   └── pom.xml                              <-- CHILD POM
├── core-user-module/
│   └── pom.xml                              <-- CHILD POM (Nghiệp vụ user)
├── core-user-infrastructure-module/
│   └── pom.xml                              <-- CHILD POM (Hạ tầng DB user)
├── core-security-module/
│   └── pom.xml                              <-- CHILD POM (Bảo mật / mã hoá)
├── api-portal-service/
│   └── pom.xml                              <-- CHILD POM (Ứng dụng chạy API)
└── ...
```

---

## 2. Chi tiết về Parent POM (`pom.xml` ở thư mục gốc)

Parent POM là "trung tâm điều hành" cấu hình cho toàn bộ dự án. Nó không chứa mã nguồn Java mà chỉ chứa cấu hình Maven.

### 2.1. Khai báo đóng gói dạng `pom`
```xml
<groupId>x10.zenfit</groupId>
<artifactId>ai-service</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>pom</packaging>
```
- `<packaging>pom</packaging>`: Bắt buộc đối với Parent POM. Báo hiệu cho Maven biết đây là project điều phối (aggregator), không sinh ra file `.jar` độc lập.

### 2.2. Khai báo danh sách module con (`<modules>`)
```xml
<modules>
    <module>commons</module>
    <module>core-user-module</module>
    <module>core-user-infrastructure-module</module>
    <module>core-security-module</module>
    <module>api-portal-service</module>
    ...
</modules>
```
- **Vai trò**: Khi bạn chạy lệnh `mvn clean compile` hoặc `mvn test` tại thư mục gốc, Maven sẽ duyệt qua tất cả các module này và tính toán thứ tự biên dịch (Maven Reactor Order) dựa vào sự phụ thuộc giữa các module con.

### 2.3. Quản lý phiên bản tập trung (`<properties>`)
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.4</spring-boot.version>
    <spring-cloud.version>2023.0.3</spring-cloud.version>
    <lombok.version>1.18.34</lombok.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>
```
- Gom tất cả các phiên bản thư viện vào biến `${...}`. Khi cần nâng cấp (ví dụ Spring Boot hay Lombok), chỉ cần sửa 1 dòng duy nhất tại đây.

### 2.4. Phân biệt cực kỳ quan trọng: `<dependencyManagement>` vs `<dependencies>`

| Thẻ | Bản chất | Có tự động tải vào module con không? | Khi nào nên dùng? |
|---|---|---|---|
| `<dependencyManagement>` | **Chỉ định danh sách quy ước**: Khai báo trước tên thư viện, version và scope. | **KHÔNG**. Module con không bị ảnh hưởng nếu không tự khai báo dùng. | Dùng cho hầu hết tất cả các thư viện: Spring Boot BOM, các module nội bộ (`commons`, `core-*`), thư viện bên thứ 3. |
| `<dependencies>` | **Áp đặt toàn cục**: Mọi module con đều **tự động kế thừa** thư viện này mà không cần khai báo gì thêm. | **CÓ**. Tất cả module con đều có sẵn thư viện này trên classpath. | Chỉ dùng cho thư viện mà **100% mọi module con đều dùng** (ví dụ: `lombok`). |

#### a. Import BOM trong `<dependencyManagement>`:
```xml
<dependencyManagement>
    <dependencies>
        <!-- Spring Boot BOM: Đồng bộ toàn bộ version các thư viện Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <!-- Quản lý version các module nội bộ -->
        <dependency>
            <groupId>x10.zenfit</groupId>
            <artifactId>commons</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>x10.zenfit</groupId>
            <artifactId>core-user-module</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>x10.zenfit</groupId>
            <artifactId>core-security-module</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### b. Dependencies dùng chung trong `<dependencies>`:
```xml
<dependencies>
    <!-- Mọi module con đều tự động có Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 2.5. `<pluginManagement>` vs `<plugins>`
Tương tự như dependencies:
- `<pluginManagement>`: Khai báo cấu hình plugin dùng chung (phiên bản `maven-compiler-plugin`, cấu hình annotation processor cho Lombok & MapStruct, `spring-boot-maven-plugin`). Module con chỉ kích hoạt khi cần.
- `<plugins>`: Kích hoạt plugin cho toàn bộ project.

---

## 3. Chi tiết về Child POM (`pom.xml` của module con)

Mỗi module con chỉ tập trung vào những gì nó cần, kế thừa toàn bộ cấu hình từ cha.

### 3.1. Kế thừa từ Parent (`<parent>`)
Mọi Child POM bắt buộc phải bắt đầu bằng thẻ `<parent>`:
```xml
<parent>
    <groupId>x10.zenfit</groupId>
    <artifactId>ai-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>

<artifactId>core-user-module</artifactId>
```
- Module con sẽ tự động nhận `groupId` và `version` của cha nếu không khai báo lại.
- Module con chỉ cần khai báo `<artifactId>` định danh cho chính nó.

### 3.2. Khai báo dependencies ở module con: **KHÔNG ghi đè `<version>`**
Vì Parent POM đã khai báo Spring Boot BOM và các internal modules trong `<dependencyManagement>`, nên trong Child POM:
```xml
<dependencies>
    <!-- Kế thừa version từ Spring Boot BOM ở Parent -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- Kế thừa version từ Parent POM -->
    <dependency>
        <groupId>x10.zenfit</groupId>
        <artifactId>commons</artifactId>
    </dependency>

    <!-- Kế thừa version từ Parent POM -->
    <dependency>
        <groupId>x10.zenfit</groupId>
        <artifactId>core-security-module</artifactId>
    </dependency>
</dependencies>
```

> [!IMPORTANT]
> **Quy tắc vàng**: Không bao giờ viết thẻ `<version>` ở file `pom.xml` con trừ trường hợp bất khả kháng. Luôn để Parent POM quyết định version!

---

## 4. Phân loại 2 dạng module con trong dự án

### 4.1. Dạng Thư viện / Domain Module (`core-*`, `commons`, `*-infra`)
- **Mục đích**: Chứa code logic nghiệp vụ, entities, repository interfaces, adapters... để các module khác import vào.
- **Packaging mặc định**: `jar` (không cần viết `<packaging>`, Maven mặc định là jar).
- **Cấu hình**: **KHÔNG** thêm plugin `spring-boot-maven-plugin`. Nếu thêm plugin này, file jar sẽ bị đóng gói dạng boot-fat-jar và các module khác sẽ không thể import class của nó.

### 4.2. Dạng Ứng dụng chạy được / Executable Service (`api-portal-service`, `api-cms-service`)
- **Mục đích**: Chứa hàm `main()` (`Application.java`), controller, cấu hình chạy ứng dụng web.
- **Cấu hình**:
  1. Phụ thuộc vào các module `core` và `infra` cần thiết.
  2. Kích hoạt `spring-boot-maven-plugin` để đóng gói thành 1 file JAR chạy được (`java -jar app.jar`):

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 5. Quy trình làm việc thực tế (Step-by-step)

### Kịch bản 1: Tạo một module mới (Ví dụ: `core-payment-module`)
1. **Bước 1**: Tạo thư mục `core-payment-module` và file `pom.xml` con có thẻ `<parent>`.
2. **Bước 2 (Tại Parent POM)**:
   - Thêm `<module>core-payment-module</module>` vào khối `<modules>`.
   - Thêm thông tin vào `<dependencyManagement>` của Parent:
     ```xml
     <dependency>
         <groupId>x10.zenfit</groupId>
         <artifactId>core-payment-module</artifactId>
         <version>${project.version}</version>
     </dependency>
     ```
3. **Bước 3**: Chạy `mvn compile` tại thư mục gốc để đảm bảo module mới được tích hợp vào reactor.

---

### Kịch bản 2: Cho module A gọi module B (Ví dụ: `core-user-module` gọi `core-security-module`)
1. **Kiểm tra Parent POM**: Đảm bảo module B (`core-security-module`) đã được khai báo trong `<dependencyManagement>` của Parent POM.
2. **Khai báo ở module A (`core-user-module/pom.xml`)**:
   Thêm dependency (không cần thẻ version):
   ```xml
   <dependency>
       <groupId>x10.zenfit</groupId>
       <artifactId>core-security-module</artifactId>
   </dependency>
   ```
3. Maven sẽ tự động phát hiện quan hệ phụ thuộc: khi build, Maven sẽ biên dịch `core-security-module` trước, sau đó mới đến `core-user-module`.

---

### Kịch bản 3: Thêm một thư viện bên thứ 3 mới (Ví dụ: thư viện `aws-java-sdk-s3`)
1. **Parent POM**:
   - Thêm version vào `<properties>`: `<aws.s3.version>1.12.700</aws.s3.version>`.
   - Thêm dependency vào `<dependencyManagement>`:
     ```xml
     <dependency>
         <groupId>com.amazonaws</groupId>
         <artifactId>aws-java-sdk-s3</artifactId>
         <version>${aws.s3.version}</version>
     </dependency>
     ```
2. **Child POM (nơi cần dùng)**:
   - Chỉ việc kéo vào:
     ```xml
     <dependency>
         <groupId>com.amazonaws</groupId>
         <artifactId>aws-java-sdk-s3</artifactId>
     </dependency>
     ```
   Các module khác không cần dùng đến AWS S3 sẽ hoàn toàn sạch sẽ, không bị kéo thư viện thừa.

---

## 6. Các lỗi thường gặp (Pitfalls) cần tránh

1. **Ghi đè version ở module con**:
   - *Hậu quả*: Khi parent nâng cấp phiên bản, module con vẫn giữ bản cũ gây xung đột runtime (`NoSuchMethodError`, `ClassNotFoundException`).
   - *Cách tránh*: Luôn để parent quản lý phiên bản qua `<dependencyManagement>`.

2. **Đặt phụ thuộc vào `<dependencies>` của Parent POM quá bừa bãi**:
   - *Hậu quả*: Đặt thư viện như MongoDB hay Spring Web vào `<dependencies>` ở cha khiến ngay cả các module domain core thuần tuý cũng bị dính framework (vi phạm Clean Architecture).
   - *Cách tránh*: Chỉ đặt công cụ hỗ trợ code toàn diện (như Lombok) vào `<dependencies>` của cha.

3. **Vòng tròn phụ thuộc (Circular Dependency)**:
   - *Hậu quả*: Module A phụ thuộc Module B, Module B lại phụ thuộc ngược lại Module A -> Maven báo lỗi không thể build.
   - *Cách tránh*: Tuân thủ luồng phụ thuộc 1 chiều: `api-*` -> `core-*-infrastructure` -> `core-*` -> `commons`.
