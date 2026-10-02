# Kiến trúc module và quy tắc đặt tên

Tài liệu này giải thích cách tổ chức module, package và đặt tên trong dự án, lấy domain **user** làm ví dụ. Các domain khác (`asset`, `soundeffect`, ...) áp dụng tương tự.

---

## 1. Tổng quan kiến trúc

Dự án theo hướng **Clean Architecture / Hexagonal (Ports & Adapters)**. Mỗi domain được tách thành 2 module Maven:

| Module | Vai trò | Biết về công nghệ? |
|---|---|---|
| `core-<domain>-module` | Nghiệp vụ: use case, event, **interface** (port) | Không (không biết Mongo, Redis, Telegram...) |
| `core-<domain>-infrastructure-module` | Hạ tầng: **cài đặt** (adapter) các interface của core | Có (Mongo, cache, HTTP client, ...) |

Quy tắc phụ thuộc, **chỉ một chiều**:

```
core-user-infrastructure-module  ───────►  core-user-module
        (adapter, implement)                  (port, usecase)
```

- Infrastructure phụ thuộc vào core, **core không bao giờ import infrastructure**.
- Core chỉ khai báo "tôi cần gì" (interface), infrastructure quyết định "làm bằng cách nào" (Mongo, Redis, Telegram...).

### Lợi ích
- Đổi công nghệ (vd Mongo sang DB khác) chỉ sửa infrastructure, không đụng nghiệp vụ.
- Unit test use case chỉ cần mock interface, không cần dựng DB.
- Nhìn vào package `usecases` là biết hệ thống làm được những gì.

---

## 2. Cấu trúc module

### 2.1. `core-user-module` (package gốc `x10.qvid.user.core`)

```
x10.qvid.user.core
├── events/            Domain event (UserLockedEvent, UserVerifiedEvent, ...)
├── repositories/      Interface truy cập dữ liệu (port): IUserRepository, IUserOtpRepository, ...
│                      + object lọc/query thuần nghiệp vụ: UserFilter
├── thirdparties/      Interface gọi dịch vụ bên ngoài (port): IMessageService
├── usecases/          Mỗi use case một package con (xem mục 3)
│   ├── createUserUc/
│   ├── changePasswordUc/
│   ├── changeStatusUc/
│   └── ...
├── ICoreUserService       Cửa vào của module (facade), controller/service ngoài gọi qua đây
└── CoreUserServiceImpl    Cài đặt facade, điều phối sang các use case
```

### 2.2. `core-user-infrastructure-module` (package gốc `x10.qvid.user.infra`)

```
x10.qvid.user.infra
├── adapters/
│   ├── repositories/          Implement các interface trong core/repositories
│   │   ├── mappers/           Chuyển đổi Entity (core) <-> Document (datasource)
│   │   ├── UserRepositoryImpl
│   │   ├── UserOtpRepositoryImpl
│   │   └── UserQueryBuilder   Dựng query từ UserFilter
│   └── thirdparties.telegram/ Implement các interface trong core/thirdparties
└── datasources/
    └── mongo/                 Document + DAO của Mongo (IUserDocumentDao, ...)
```

### 2.3. Luồng gọi

```
Controller
   └─► ICoreUserService (core)
          └─► CreateUserUc (core/usecases)
                 └─► IUserRepository (core, interface)
                        ▲ implement
                 UserRepositoryImpl (infra/adapters)
                        └─► IUserDocumentDao (infra/datasources/mongo)
```

---

## 3. Package `usecases`

Mỗi use case là **một hành động nghiệp vụ**, nằm trong **một package riêng** chứa tất cả class liên quan:

```
usecases/
└── createUserUc/
    ├── CreateUserUc        Logic xử lý chính
    └── CreateUserUcReq     Input
```

Trường hợp cần output riêng thì thêm `...Resp`:

```
usecases/getOtpByTelegramUc/
    ├── GetOtpByTelegramUc
    ├── GetOtpByTelegramUcReq
    └── GetOtpByTelegramResp
```

Trách nhiệm của một use case:
1. Nhận input (`...Req`).
2. Kiểm tra rule nghiệp vụ, điều phối entity và các interface (`IUserRepository`, `IMessageService`, ...).
3. Trả kết quả (entity hoặc `...Resp`), phát event nếu cần.

Use case **không** chứa code SQL/Mongo, HTTP, annotation của thư viện hạ tầng.

---

## 4. Quy tắc đặt tên

### 4.1. Module (Maven)

Dạng: `core-<domain>-module` và `core-<domain>-infrastructure-module`, chữ thường, nối bằng `-`.

Ví dụ: `core-user-module`, `core-user-infrastructure-module`, `core-soundeffect-module`.

### 4.2. Package

| Loại | Quy tắc | Ví dụ |
|---|---|---|
| Package gốc core | `x10.qvid.<domain>.core` | `x10.qvid.user.core` |
| Package gốc infra | `x10.qvid.<domain>.infra` | `x10.qvid.user.infra` |
| Package theo vai trò | chữ thường, số nhiều | `events`, `repositories`, `thirdparties`, `usecases`, `adapters`, `datasources`, `mappers` |
| Package của 1 use case | camelCase, kết thúc bằng `Uc` | `createUserUc`, `changePasswordUc` |

### 4.3. Class và Interface

| Loại | Quy tắc | Ví dụ |
|---|---|---|
| Interface | Tiền tố `I` + PascalCase | `IUserRepository`, `IMessageService`, `IUserEntityMapper` |
| Cài đặt interface | Tên interface bỏ `I`, thêm `Impl` | `UserRepositoryImpl`, `CoreUserServiceImpl` |
| Use case | `<ĐộngTừ><DanhTừ>Uc` | `CreateUserUc`, `ChangeStatusUc` |
| Input / Output use case | `<TênUseCase>Req` / `<...>Resp` | `CreateUserUcReq`, `GetOtpByTelegramResp` |
| Event | `<Entity><QuáKhứ>Event` | `UserLockedEvent`, `UserVerifiedEvent` |
| Entity (domain) | `<Danh từ>Entity` | `UserEntity` |
| Document (Mongo) | `<Danh từ>Document` | `BankDocument` |
| DAO (Mongo) | `I<Danh từ>DocumentDao` | `IUserDocumentDao`, `IRoleDocumentDao` |
| Mapper | `I<Danh từ>EntityMapper` | `IUserEntityMapper` |
| Facade của module | `ICore<Domain>Service` / `Core<Domain>ServiceImpl` | `ICoreUserService` |

### 4.4. Method

camelCase, bắt đầu bằng động từ. Quy ước thường dùng trong `ICoreUserService`:

| Tiền tố | Ý nghĩa | Ví dụ |
|---|---|---|
| `create` | Tạo mới | `create(CreateUserUcReq req)` |
| `getById`, `getByUsername` | Lấy một bản ghi theo điều kiện | `getById(String id)` |
| `getByFilter` | Lấy danh sách theo bộ lọc | `getByFilter(UserFilter req, MSort sort)` |
| `update` | Cập nhật | `update(UpdateUserUcReq req)` |
| `change...` | Đổi một thuộc tính/trạng thái | `changeStatus(String userId, byte status)` |

### 4.5. File tài liệu (`docs/`)

- Định dạng `.md`, tên **kebab-case**, chữ thường, không dấu, không khoảng trắng.
- Ví dụ: `architecture-and-naming-conventions.md`.

---

## 5. Checklist khi thêm chức năng mới

Ví dụ thêm use case "khóa tài khoản":

1. **Core**: tạo package `usecases/lockUserUc/` gồm `LockUserUc`, `LockUserUcReq`.
2. Nếu cần dữ liệu/dịch vụ mới: thêm method vào interface `I...Repository` hoặc `I...Service` trong core. **Chỉ khai báo interface.**
3. Nếu có sự kiện: thêm `UserLockedEvent` vào `events/`.
4. Thêm method vào `ICoreUserService` và gọi use case trong `CoreUserServiceImpl`.
5. **Infrastructure**: cài đặt method vừa khai báo trong `...RepositoryImpl` (hoặc adapter tương ứng), thêm mapper/DAO nếu cần.
6. Viết unit test cho use case (mock các interface).

---

## 6. Những điều cần tránh

- Không import class từ `infra` vào `core`.
- Không để annotation/kiểu dữ liệu của Mongo (`@Document`, `ObjectId`, ...) lọt vào entity hoặc use case của core.
- Không viết logic nghiệp vụ trong adapter hoặc controller, logic chỉ nằm trong use case.
- Không gom nhiều hành động khác nhau vào một use case; một use case làm một việc.
- Không đặt tên viết tắt khó hiểu hoặc sai chính tả (vd `Bt` thay cho `By`).

---

## 7. Các điểm chưa nhất quán cần chỉnh (kiểm tra lại trong code hiện tại)

| Hiện tại | Nên là | Lý do |
|---|---|---|
| `GetOtpBtTelegramResp` | `GetOtpByTelegramResp` | Sai chính tả `Bt` |
| `RefreshTokenMapper` | `IRefreshTokenMapper` | Các mapper khác đều có tiền tố `I` |
| `RefreshTokenRepository` (trong infra) | `RefreshTokenRepositoryImpl` | Cài đặt của `IRefreshTokenRepository` cần hậu tố `Impl` |
| `IFacebookPixelCookiesCache` nằm trong `repositories` | cân nhắc giữ hoặc chuyển sang package `caches` | Cache không hẳn là repository, nên thống nhất trong team |