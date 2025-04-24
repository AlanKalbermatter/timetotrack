# ✅ TimeToTrack - TODO List

## 🏁 Completed

- [x] Add `init_db.sh` script for DB setup
- [x] Set up PostgreSQL database `goldentimer`
- [x] Create tables: user, customer, projects, time_entry
- [x] Configure JDBC connection
- [x] Implement `GET /api/users`
- [x] Modularize code: DAO + Service + API Verticle
- [x] Unit test for `UserService`
- [x] Integration test for `UserDao`
- [x] API test for `UserApiVerticle`

## 🔨 In Progress / Next Steps

- [ ] Implement `POST /api/users`
- [ ] Add input validation
- [ ] Implement `GET /api/users/{id}`
- [ ] Create `POST /api/time-entries`
- [ ] Create `PUT /api/time-entries/{id}` (to finish an entry)
- [ ] Create `GET /api/users/{id}/time-entries`
- [ ] Add Swagger (OpenAPI) documentation
- [ ] Add structured logging
- [ ] Build a React-based frontend dashboard
- [ ] Implement basic JWT authentication

## 💡 Future Ideas

- [ ] Export reports (CSV, PDF)
- [ ] Google Calendar integration
- [ ] Pomodoro-style notifications
- [ ] Mobile UI with React Native
