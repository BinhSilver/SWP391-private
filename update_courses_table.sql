-- -- Cập nhật bảng Courses để thêm cột CreatedBy
-- -- Chạy script này để hoàn thiện luồng xác nhận giáo viên

-- -- Thêm cột CreatedBy vào bảng Courses
-- ALTER TABLE Courses ADD CreatedBy INT;

-- -- Thêm foreign key constraint
-- ALTER TABLE Courses ADD CONSTRAINT FK_Courses_Users 
-- FOREIGN KEY (CreatedBy) REFERENCES Users(UserID);

-- -- Cập nhật dữ liệu hiện có (nếu có)
-- -- Giả sử tất cả khóa học hiện tại được tạo bởi admin (UserID = 1)
-- UPDATE Courses SET CreatedBy = 1 WHERE CreatedBy IS NULL;

-- -- Đảm bảo cột CreatedBy không null cho các khóa học mới
-- ALTER TABLE Courses ALTER COLUMN CreatedBy INT NOT NULL;

-- -- Thêm index để tối ưu hiệu suất truy vấn
-- CREATE INDEX IX_Courses_CreatedBy ON Courses(CreatedBy);

-- PRINT 'Đã cập nhật bảng Courses thành công!';
-- PRINT 'Cột CreatedBy đã được thêm và liên kết với bảng Users.'; 