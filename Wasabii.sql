-- =========================
-- WASABII - JAPANESE LEARNING PLATFORM
-- DATABASE SCHEMA & SAMPLE DATA
-- =========================

-- =========================
-- 1. TẠO DATABASE
-- =========================
IF DB_ID('Wasabii') IS NOT NULL
    DROP DATABASE Wasabii;
GO
CREATE DATABASE Wasabii;
GO
USE Wasabii;
GO

-- =========================
-- 2. TẠO BẢNG CHÍNH
-- =========================

-- Bảng Roles
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY,
    RoleName NVARCHAR(50) UNIQUE NOT NULL
);

-- Bảng PremiumPlans
CREATE TABLE PremiumPlans (
    PlanID INT PRIMARY KEY IDENTITY,
    PlanName NVARCHAR(100),
    Price DECIMAL(10, 2),
    DurationInMonths INT,
    Description NVARCHAR(255)
);

-- Bảng Users
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY,
    RoleID INT FOREIGN KEY REFERENCES Roles(RoleID),
    Email NVARCHAR(255) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255),
    GoogleID NVARCHAR(255),
    FullName NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1,
    IsLocked BIT DEFAULT 0,
    BirthDate DATE,
    PhoneNumber NVARCHAR(20),
    JapaneseLevel NVARCHAR(50),
    Address NVARCHAR(255),
    Country NVARCHAR(100),
    Avatar NVARCHAR(MAX),
    Gender NVARCHAR(10) CONSTRAINT DF_Users_Gender DEFAULT N'Khác',
    IsTeacherPending BIT DEFAULT 0,
    CertificatePath NVARCHAR(500)
);

-- Bảng Payments
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    PlanID INT NOT NULL FOREIGN KEY REFERENCES PremiumPlans(PlanID),
    Amount FLOAT NOT NULL,
    PaymentDate DATETIME NULL,
    TransactionNo NVARCHAR(100) NULL,
    OrderInfo NVARCHAR(255),
    ResponseCode NVARCHAR(20) NULL,
    TransactionStatus NVARCHAR(50),
    OrderCode BIGINT,
    CheckoutUrl NVARCHAR(500),
    Status NVARCHAR(50),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng UserPremium
CREATE TABLE UserPremium (
    UserID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    PlanID INT NOT NULL FOREIGN KEY REFERENCES PremiumPlans(PlanID),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    PRIMARY KEY (UserID, PlanID, StartDate)
);

-- Bảng Courses
CREATE TABLE Courses (
    CourseID INT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(MAX),
    IsHidden BIT DEFAULT 0,
    IsSuggested BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CreatedBy INT NULL FOREIGN KEY REFERENCES Users(UserID),
    imageUrl NVARCHAR(MAX) NULL
);

-- Bảng Enrollment
CREATE TABLE Enrollment (
    EnrollmentID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    EnrolledAt DATETIME DEFAULT GETDATE()
);

-- Bảng Lessons
CREATE TABLE Lessons (
    LessonID INT PRIMARY KEY IDENTITY,
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Title NVARCHAR(255),
    Description NVARCHAR(1000),
    IsHidden BIT DEFAULT 0,
    OrderIndex INT DEFAULT 0
);

-- Bảng LessonMaterials
CREATE TABLE LessonMaterials (
    MaterialID INT PRIMARY KEY IDENTITY(1,1),
    LessonID INT NOT NULL FOREIGN KEY REFERENCES Lessons(LessonID),
    MaterialType NVARCHAR(50) NOT NULL,
    FileType NVARCHAR(50) NOT NULL,
    Title NVARCHAR(255),
    FilePath NVARCHAR(MAX),
    IsHidden BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Vocabulary
CREATE TABLE Vocabulary (
    VocabID INT PRIMARY KEY IDENTITY,
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    Reading NVARCHAR(100),
    Example NVARCHAR(MAX),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    imagePath VARCHAR(255) DEFAULT NULL
);

-- Bảng Tags
CREATE TABLE Tags (
    TagID INT PRIMARY KEY IDENTITY,
    TagName NVARCHAR(50) UNIQUE NOT NULL
);

-- Bảng UserVocabulary
CREATE TABLE UserVocabulary (
    UserVocabID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Kanji
CREATE TABLE Kanji (
    KanjiID INT PRIMARY KEY IDENTITY,
    Character NVARCHAR(10),
    Onyomi NVARCHAR(100),
    Kunyomi NVARCHAR(100),
    Meaning NVARCHAR(255),
    StrokeCount INT,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID)
);

-- Bảng Flashcards
CREATE TABLE Flashcards (
    FlashcardID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Title NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    IsPublic BIT DEFAULT 0,
    Description NVARCHAR(500),
    CoverImage NVARCHAR(500),
    CourseID INT NULL FOREIGN KEY REFERENCES Courses(CourseID)
);

-- Bảng FlashcardItems
CREATE TABLE FlashcardItems (
    FlashcardItemID INT PRIMARY KEY IDENTITY,
    FlashcardID INT FOREIGN KEY REFERENCES Flashcards(FlashcardID),
    VocabID INT NULL FOREIGN KEY REFERENCES Vocabulary(VocabID),
    UserVocabID INT NULL FOREIGN KEY REFERENCES UserVocabulary(UserVocabID),
    Note NVARCHAR(255),
    FrontContent NVARCHAR(500),
    BackContent NVARCHAR(500),
    FrontImage NVARCHAR(500),
    BackImage NVARCHAR(500),
    OrderIndex INT DEFAULT 0
);

-- Bảng Quizzes
CREATE TABLE Quizzes (
    QuizID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255)
);

-- Bảng Questions
CREATE TABLE Questions (
    QuestionID INT PRIMARY KEY IDENTITY,
    QuizID INT NULL FOREIGN KEY REFERENCES Quizzes(QuizID),
    QuestionText NVARCHAR(MAX),
    TimeLimit INT DEFAULT 30
);

-- Bảng Answers
CREATE TABLE Answers (
    AnswerID INT PRIMARY KEY IDENTITY,
    QuestionID INT FOREIGN KEY REFERENCES Questions(QuestionID),
    AnswerText NVARCHAR(MAX),
    IsCorrect BIT,
    AnswerNumber INT CHECK (AnswerNumber BETWEEN 1 AND 4)
);

-- Bảng QuizResults
CREATE TABLE QuizResults (
    ResultID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    QuizID INT FOREIGN KEY REFERENCES Quizzes(QuizID),
    Score INT,
    TakenAt DATETIME DEFAULT GETDATE()
);

-- Bảng Conversations
CREATE TABLE Conversations (
    ConversationID INT PRIMARY KEY IDENTITY(1,1),
    User1ID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    User2ID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Messages
CREATE TABLE Messages (
    MessageID INT PRIMARY KEY IDENTITY(1,1),
    ConversationID INT NOT NULL FOREIGN KEY REFERENCES Conversations(ConversationID),
    SenderID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    Content NVARCHAR(MAX) NOT NULL,
    Type NVARCHAR(50) NOT NULL,
    IsRead BIT DEFAULT 0,
    IsRecall BIT DEFAULT 0,
    SentAt DATETIME DEFAULT GETDATE()
);

-- Bảng Blocks
CREATE TABLE Blocks (
    BlockerID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    BlockedID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    CreatedAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (BlockerID, BlockedID)
);

-- Bảng LessonAccess
CREATE TABLE LessonAccess (
    AccessID INT IDENTITY PRIMARY KEY,
    UserID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    LessonID INT NOT NULL FOREIGN KEY REFERENCES Lessons(LessonID),
    AccessedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT UC_User_Lesson UNIQUE (UserID, LessonID)
);

-- Bảng LessonVocabulary
CREATE TABLE LessonVocabulary (
    LessonID INT NOT NULL FOREIGN KEY REFERENCES Lessons(LessonID),
    VocabID INT NOT NULL FOREIGN KEY REFERENCES Vocabulary(VocabID),
    PRIMARY KEY (LessonID, VocabID)
);

-- Bảng Feedbacks
CREATE TABLE Feedbacks (
    FeedbackID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Content NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
    Rating INT CHECK (Rating BETWEEN 1 AND 5) NOT NULL DEFAULT 5
);

-- Bảng FeedbackVotes
CREATE TABLE FeedbackVotes (
    VoteID INT PRIMARY KEY IDENTITY,
    FeedbackID INT FOREIGN KEY REFERENCES Feedbacks(FeedbackID),
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    VoteType INT CHECK (VoteType IN (1, -1)) -- 1: like, -1: dislike
);

-- Bảng CourseRatings (backup table)
CREATE TABLE CourseRatings (
    RatingID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    RatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Progress
CREATE TABLE Progress (
    ProgressID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    CompletionPercent INT CHECK (CompletionPercent BETWEEN 0 AND 100),
    LastAccessed DATETIME DEFAULT GETDATE()
);

-- =========================
-- 3. SEED DATA - ROLES & PLANS
-- =========================

-- Thêm Roles
INSERT INTO Roles VALUES 
    (1, 'Free'), 
    (2, 'Premium'), 
    (3, 'Teacher'), 
    (4, 'Admin');

-- Thêm PremiumPlans
INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description)
VALUES 
    (N'Gói Tháng', 25000, 1, N'Sử dụng Premium trong 1 tháng'),
    (N'Gói Năm', 250000, 12, N'Sử dụng Premium trong 12 tháng');

-- =========================
-- 4. SEED DATA - USERS
-- =========================

-- Thêm users chính
INSERT INTO Users (RoleID, Email, PasswordHash, GoogleID, FullName, BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Avatar, Gender)
VALUES 
    (1, 'nguyenphamthanhbinh02@gmail.com', '123456789', NULL, N'Người Dùng', '2000-01-01', '0123456789', N'N5', N'Địa chỉ user', N'Việt Nam', NULL, N'Nam'),
    (4, 'huyphw2@gmail.com', '12345678', NULL, N'Huy Phan', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nam'),
    (4, 'admin@gmail.com', '123456789', NULL, N'TBinh', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nam'),
    (3, 'teacher@gmail.com', '123', NULL, N'Tanaka Sensei', '2004-07-04', '0911053612', 'N4', N'Địa chỉ teacher', N'Việt Nam', NULL, N'Nữ');

-- Thêm 50 users mẫu
DECLARE @StartDate DATETIME = '2025-01-01';
DECLARE @EndDate DATETIME = '2025-06-22';
DECLARE @DaysDiff INT = DATEDIFF(DAY, @StartDate, @EndDate);
DECLARE @UserCount INT = 50;
DECLARE @Counter INT = 1;
DECLARE @RoleID INT;
DECLARE @Email NVARCHAR(255);
DECLARE @FullName NVARCHAR(100);
DECLARE @CreatedAt DATETIME;

-- Tạm lưu danh sách người dùng để dùng cho Enrollment
CREATE TABLE #TempUsers (UserID INT, CreatedAt DATETIME);

WHILE @Counter <= @UserCount
BEGIN
    -- Phân bố RoleID theo tỷ lệ: Admin (1), Free (4), Premium (3), Teacher (1)
    SET @RoleID = CASE 
        WHEN @Counter <= 5 THEN 4 -- 5 Admin
        WHEN @Counter <= 25 THEN 1 -- 20 Free
        WHEN @Counter <= 40 THEN 2 -- 15 Premium
        ELSE 3 -- 5 Teacher
    END;

    -- Tạo email và tên giả lập
    SET @Email = 'user' + CAST(@Counter AS NVARCHAR(10)) + '@example.com';
    SET @FullName = N'Người Dùng ' + CAST(@Counter AS NVARCHAR(10));

    -- Tính CreatedAt trải đều từ 01/01/2025 đến 06/22/2025
    SET @CreatedAt = DATEADD(DAY, (@DaysDiff * (@Counter - 1)) / @UserCount, @StartDate);

    -- Thêm người dùng
    INSERT INTO Users (
        RoleID, Email, PasswordHash, FullName, CreatedAt, 
        BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Gender
    )
    VALUES (
        @RoleID, 
        @Email, 
        'hashed_password', -- Giả lập mật khẩu
        @FullName, 
        @CreatedAt,
        '2000-01-01', -- Ngày sinh giả lập
        '0123456789', -- Số điện thoại giả lập
        N'N5', 
        N'Địa chỉ giả lập', 
        N'Việt Nam', 
        CASE WHEN @Counter % 2 = 0 THEN N'Nam' ELSE N'Nữ' END
    );

    -- Lưu UserID và CreatedAt vào bảng tạm
    INSERT INTO #TempUsers (UserID, CreatedAt)
    VALUES (SCOPE_IDENTITY(), @CreatedAt);

    SET @Counter = @Counter + 1;
END;

-- =========================
-- 5. SEED DATA - COURSES & LESSONS
-- =========================

-- Lấy UserID của giáo viên
DECLARE @TeacherID INT = (SELECT UserID FROM Users WHERE Email = 'teacher@gmail.com');

-- Tạo khóa học với CreatedBy là giáo viên
INSERT INTO Courses (Title, Description, IsHidden, IsSuggested, CreatedBy, imageUrl)
VALUES (N'Khóa học Giao tiếp N5', N'Khóa học tiếng Nhật sơ cấp tập trung vào giao tiếp cơ bản.', 0, 1, @TeacherID, N'image/N5thumbnail.jpg');
DECLARE @CourseID INT = SCOPE_IDENTITY();

-- Thêm lessons
INSERT INTO Lessons (CourseID, Title, Description, IsHidden)
VALUES 
    (@CourseID, N'Bài 1: Giới thiệu bản thân', N'Nội dung bài 1', 0),
    (@CourseID, N'Bài 2: Hỏi thăm sức khỏe', N'Nội dung bài 2', 0),
    (@CourseID, N'Bài 3: Hỏi đường đi', N'Nội dung bài 3', 0);

DECLARE @LessonID1 INT = (SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân');
DECLARE @LessonID2 INT = (SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe');
DECLARE @LessonID3 INT = (SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi');

-- Thêm lesson materials
INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath, IsHidden)
VALUES 
    (@LessonID1, N'Từ vựng', N'PDF', N'Từ vựng Bài 1', N'files/courseN5/vocabN5/Lesson1.pdf', 0),
    (@LessonID1, N'Kanji', N'PDF', N'Kanji Bài 1', N'files/courseN5/kanjiN5/Lesson8.pdf', 0),
    (@LessonID1, N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 1', N'files/courseN5/grammarN5/Lesson1.pdf', 0),
    (@LessonID1, N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 1', N'files/lesson8_grammarVideo_1752740459670.mp4', 0),
    (@LessonID2, N'Từ vựng', N'PDF', N'Từ vựng Bài 2', N'files/courseN5/vocabN5/Lesson2.pdf', 0),
    (@LessonID2, N'Kanji', N'PDF', N'Kanji Bài 2', N'files/courseN5/kanjiN5/Lesson2.pdf', 0),
    (@LessonID2, N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 2', N'files/courseN5/grammarN5/Lesson2.pdf', 0),
    (@LessonID2, N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 2', N'files/lesson8_grammarVideo_1752740459670.mp4', 0),
    (@LessonID3, N'Từ vựng', N'PDF', N'Từ vựng Bài 3', N'files/courseN5/vocabN5/Lesson3.pdf', 0),
    (@LessonID3, N'Kanji', N'PDF', N'Kanji Bài 3', N'files/courseN5/kanjiN5/Lesson3.pdf', 0),
    (@LessonID3, N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 3', N'files/courseN5/grammarN5/Lesson3.pdf', 0),
    (@LessonID3, N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 3', N'files/lesson8_grammarVideo_1752740459670.mp4', 0);

-- Thêm vocabulary
INSERT INTO Vocabulary ([Word], [Meaning], [Reading], [Example], [LessonID], [imagePath])
VALUES 
    (N'水', N'nước', N'みず (mizu)', N'水を飲みます。', @LessonID1, N'imgvocab/mizu.png'),
    (N'食べる', N'ăn', N'たべる (taberu)', N'パンを食べます。', @LessonID1, N'imgvocab/taberu.png'),
    (N'学生', N'học sinh, sinh viên', N'がくせい (gakusei)', N'私は学生です。', @LessonID1, N'imgvocab/gakusei.jpeg');

-- =========================
-- 6. SEED DATA - QUIZZES & QUESTIONS
-- =========================

-- Thêm quiz
INSERT INTO Quizzes (LessonID, Title)
VALUES (@LessonID1, N'Quiz Bài 1');
DECLARE @QuizID INT = SCOPE_IDENTITY();

-- Thêm questions
INSERT INTO Questions (QuizID, QuestionText, TimeLimit)
VALUES 
    (@QuizID, N'Từ 「水」 có nghĩa là gì?', 30),
    (@QuizID, N'Từ 「食べる」 đọc là gì?', 30);

DECLARE @Q1 INT = (SELECT QuestionID FROM Questions WHERE QuestionText = N'Từ 「水」 có nghĩa là gì?');
DECLARE @Q2 INT = (SELECT QuestionID FROM Questions WHERE QuestionText = N'Từ 「食べる」 đọc là gì?');

-- Thêm answers
INSERT INTO Answers (QuestionID, AnswerText, IsCorrect, AnswerNumber)
VALUES 
    (@Q1, N'nước', 1, 1),
    (@Q1, N'lửa', 0, 2),
    (@Q1, N'cơm', 0, 3),
    (@Q1, N'cá', 0, 4),
    (@Q2, N'たべる (taberu)', 1, 1),
    (@Q2, N'のむ (nomu)', 0, 2),
    (@Q2, N'いく (iku)', 0, 3),
    (@Q2, N'みる (miru)', 0, 4);

-- Thêm quiz result
INSERT INTO QuizResults (UserID, QuizID, Score)
VALUES (1, @QuizID, 100);

-- =========================
-- 7. SEED DATA - ENROLLMENTS
-- =========================

-- Lấy CourseID của khóa học N5
DECLARE @CourseID INT = (SELECT CourseID FROM Courses WHERE Title = N'Khóa học Giao tiếp N5');

-- Khai báo biến cho enrollment
DECLARE @EnrollmentStartDate DATETIME = '2025-01-01';
DECLARE @EnrollmentEndDate DATETIME = '2025-06-22';
DECLARE @EnrollmentDaysDiff INT = DATEDIFF(DAY, @EnrollmentStartDate, @EnrollmentEndDate);

-- Thêm enrollment cho user đầu tiên
INSERT INTO Enrollment (UserID, CourseID)
VALUES (1, @CourseID);

-- Thêm 30 bản ghi Enrollment
DECLARE @EnrollmentCount INT = 30;
DECLARE @EnrollmentCounter INT = 1;
DECLARE @EnrollmentUserID INT;
DECLARE @EnrollmentCreatedAt DATETIME;
DECLARE @EnrolledAt DATETIME;

-- Cursor để lấy ngẫu nhiên 30 người dùng từ bảng tạm
DECLARE user_cursor CURSOR FOR 
SELECT TOP 30 UserID, CreatedAt 
FROM #TempUsers 
ORDER BY NEWID(); -- Randomize

OPEN user_cursor;
FETCH NEXT FROM user_cursor INTO @EnrollmentUserID, @EnrollmentCreatedAt;

WHILE @EnrollmentCounter <= @EnrollmentCount
BEGIN
    -- Tính EnrolledAt trải đều từ 01/01/2025 đến 06/22/2025
    SET @EnrolledAt = DATEADD(DAY, (@EnrollmentDaysDiff * (@EnrollmentCounter - 1)) / @EnrollmentCount, @EnrollmentStartDate);

    -- Thêm bản ghi Enrollment
    INSERT INTO Enrollment (UserID, CourseID, EnrolledAt)
    VALUES (@EnrollmentUserID, @CourseID, @EnrolledAt);

    FETCH NEXT FROM user_cursor INTO @EnrollmentUserID, @EnrollmentCreatedAt;
    SET @EnrollmentCounter = @EnrollmentCounter + 1;
END;

CLOSE user_cursor;
DEALLOCATE user_cursor;

-- Xóa bảng tạm
DROP TABLE #TempUsers;


-- =========================
-- 8. SEED DATA - PREMIUM USERS & PAYMENTS
-- =========================

DECLARE @PremiumStartDate DATETIME = '2025-07-01';
DECLARE @PremiumEndDate DATETIME = '2025-08-20';
DECLARE @PremiumDaysDiff INT = DATEDIFF(DAY, @PremiumStartDate, @PremiumEndDate);
DECLARE @PremiumRecordCount INT = 30;
DECLARE @PremiumCounter INT = 1;
DECLARE @PremiumUserID INT;
DECLARE @PlanID INT;
DECLARE @StartDateRecord DATETIME;
DECLARE @EndDateRecord DATETIME;
DECLARE @Amount FLOAT;
DECLARE @PaymentDate DATETIME;
DECLARE @PremiumCreatedAt DATETIME;

-- Tạo bảng tạm để lưu 30 người dùng Premium được chọn ngẫu nhiên
CREATE TABLE #TempPremiumUsers (UserID INT, RowNum INT);

-- Chọn 30 người dùng, ưu tiên RoleID = 2 (Premium), nếu không đủ thì lấy thêm từ các role khác
INSERT INTO #TempPremiumUsers (UserID, RowNum)
SELECT UserID, ROW_NUMBER() OVER (ORDER BY NEWID()) AS RowNum
FROM Users
WHERE RoleID = 2
UNION
SELECT UserID, ROW_NUMBER() OVER (ORDER BY NEWID()) AS RowNum
FROM Users
WHERE RoleID != 2
ORDER BY RowNum
OFFSET 0 ROWS FETCH NEXT 30 ROWS ONLY;

-- Cursor để duyệt qua 30 người dùng được chọn
DECLARE premium_cursor CURSOR FOR 
SELECT UserID
FROM #TempPremiumUsers
ORDER BY RowNum;

OPEN premium_cursor;
FETCH NEXT FROM premium_cursor INTO @PremiumUserID;

WHILE @PremiumCounter <= @PremiumRecordCount
BEGIN
    -- Chọn ngẫu nhiên PlanID (1: Gói Tháng, 2: Gói Năm)
    SET @PlanID = CASE WHEN RAND() > 0.5 THEN 1 ELSE 2 END;

    -- Tính CreatedAt, StartDate, và PaymentDate trải đều từ 01/07/2025 đến 30/07/2025
    SET @PremiumCreatedAt = DATEADD(DAY, (@PremiumDaysDiff * (@PremiumCounter - 1)) / @PremiumRecordCount, @PremiumStartDate);
    SET @StartDateRecord = @PremiumCreatedAt;
    SET @PaymentDate = @PremiumCreatedAt;

    -- Lấy thông tin giá và thời hạn từ PremiumPlans
    SELECT @Amount = Price, 
           @EndDateRecord = DATEADD(MONTH, DurationInMonths, @StartDateRecord)
    FROM PremiumPlans
    WHERE PlanID = @PlanID;

    -- Thêm bản ghi vào UserPremium
    INSERT INTO UserPremium (UserID, PlanID, StartDate, EndDate)
    VALUES (@PremiumUserID, @PlanID, @StartDateRecord, @EndDateRecord);

    -- Thêm bản ghi vào Payments
    INSERT INTO Payments (
        UserID, 
        PlanID, 
        Amount, 
        PaymentDate, 
        TransactionNo, 
        OrderInfo, 
        TransactionStatus, 
        Status, 
        CreatedAt
    )
    VALUES (
        @PremiumUserID, 
        @PlanID, 
        @Amount, 
        @PaymentDate, 
        'TXN' + RIGHT('000000' + CAST(@PremiumCounter AS NVARCHAR(6)), 6), 
        N'Thanh toán ' + (SELECT PlanName FROM PremiumPlans WHERE PlanID = @PlanID), 
        'SUCCESS', 
        'COMPLETED', 
        @PremiumCreatedAt
    );

    FETCH NEXT FROM premium_cursor INTO @PremiumUserID;
    SET @PremiumCounter = @PremiumCounter + 1;
END;

-- Dọn dẹp
CLOSE premium_cursor;
DEALLOCATE premium_cursor;


DROP TABLE #TempPremiumUsers;

-- =========================
-- 9. SEED DATA - FEEDBACKS (RATINGS)
-- =========================

-- Lấy CourseID của khóa học N5
DECLARE @CourseID INT = (SELECT CourseID FROM Courses WHERE Title = N'Khóa học Giao tiếp N5');

-- Thêm một số đánh giá mẫu cho khóa học vào bảng Feedbacks
INSERT INTO Feedbacks (UserID, CourseID, Content, Rating, CreatedAt)
VALUES 
    (1, @CourseID, N'Khóa học rất hay và dễ hiểu!', 5, '2025-01-15'),
    (2, @CourseID, N'Giáo viên giảng dạy rất nhiệt tình', 4, '2025-01-20'),
    (3, @CourseID, N'Nội dung phù hợp với người mới bắt đầu', 5, '2025-02-01'),
    (4, @CourseID, N'Bài tập thực hành rất hữu ích', 4, '2025-02-10'),
    (5, @CourseID, N'Cách giảng dạy rất dễ hiểu', 5, '2025-02-15'),
    (6, @CourseID, N'Tốt nhưng cần thêm bài tập', 3, '2025-03-01'),
    (7, @CourseID, N'Rất hài lòng với khóa học này', 5, '2025-03-10'),
    (8, @CourseID, N'Giáo trình rất chi tiết', 4, '2025-03-20'),
    (9, @CourseID, N'Khuyến nghị cho người mới học', 5, '2025-04-01'),
    (10, @CourseID, N'Thời gian học tập linh hoạt', 4, '2025-04-15');

-- =========================
-- 10. HÀM XỬ LÝ DẤU TIẾNG VIỆT
-- =========================

CREATE OR ALTER FUNCTION dbo.RemoveDiacritics(@input NVARCHAR(MAX))
RETURNS NVARCHAR(MAX)
AS
BEGIN
    DECLARE @result NVARCHAR(MAX) = LOWER(@input);
    SET @result = REPLACE(@result, N'à', 'a');
    SET @result = REPLACE(@result, N'á', 'a');
    SET @result = REPLACE(@result, N'ả', 'a');
    SET @result = REPLACE(@result, N'ã', 'a');
    SET @result = REPLACE(@result, N'ạ', 'a');
    SET @result = REPLACE(@result, N'ă', 'a');
    SET @result = REPLACE(@result, N'ằ', 'a');
    SET @result = REPLACE(@result, N'ắ', 'a');
    SET @result = REPLACE(@result, N'ẳ', 'a');
    SET @result = REPLACE(@result, N'ẵ', 'a');
    SET @result = REPLACE(@result, N'ặ', 'a');
    SET @result = REPLACE(@result, N'â', 'a');
    SET @result = REPLACE(@result, N'ầ', 'a');
    SET @result = REPLACE(@result, N'ấ', 'a');
    SET @result = REPLACE(@result, N'ẩ', 'a');
    SET @result = REPLACE(@result, N'ẫ', 'a');
    SET @result = REPLACE(@result, N'ậ', 'a');
    SET @result = REPLACE(@result, N'đ', 'd');
    SET @result = REPLACE(@result, N'è', 'e');
    SET @result = REPLACE(@result, N'é', 'e');
    SET @result = REPLACE(@result, N'ẻ', 'e');
    SET @result = REPLACE(@result, N'ẽ', 'e');
    SET @result = REPLACE(@result, N'ẹ', 'e');
    SET @result = REPLACE(@result, N'ê', 'e');
    SET @result = REPLACE(@result, N'ề', 'e');
    SET @result = REPLACE(@result, N'ế', 'e');
    SET @result = REPLACE(@result, N'ể', 'e');
    SET @result = REPLACE(@result, N'ễ', 'e');
    SET @result = REPLACE(@result, N'ệ', 'e');
    SET @result = REPLACE(@result, N'ì', 'i');
    SET @result = REPLACE(@result, N'í', 'i');
    SET @result = REPLACE(@result, N'ỉ', 'i');
    SET @result = REPLACE(@result, N'ĩ', 'i');
    SET @result = REPLACE(@result, N'ị', 'i');
    SET @result = REPLACE(@result, N'ò', 'o');
    SET @result = REPLACE(@result, N'ó', 'o');
    SET @result = REPLACE(@result, N'ỏ', 'o');
    SET @result = REPLACE(@result, N'õ', 'o');
    SET @result = REPLACE(@result, N'ọ', 'o');
    SET @result = REPLACE(@result, N'ô', 'o');
    SET @result = REPLACE(@result, N'ồ', 'o');
    SET @result = REPLACE(@result, N'ố', 'o');
    SET @result = REPLACE(@result, N'ổ', 'o');
    SET @result = REPLACE(@result, N'ỗ', 'o');
    SET @result = REPLACE(@result, N'ộ', 'o');
    SET @result = REPLACE(@result, N'ơ', 'o');
    SET @result = REPLACE(@result, N'ờ', 'o');
    SET @result = REPLACE(@result, N'ớ', 'o');
    SET @result = REPLACE(@result, N'ở', 'o');
    SET @result = REPLACE(@result, N'ỡ', 'o');
    SET @result = REPLACE(@result, N'ợ', 'o');
    SET @result = REPLACE(@result, N'ù', 'u');
    SET @result = REPLACE(@result, N'ú', 'u');
    SET @result = REPLACE(@result, N'ủ', 'u');
    SET @result = REPLACE(@result, N'ũ', 'u');
    SET @result = REPLACE(@result, N'ụ', 'u');
    SET @result = REPLACE(@result, N'ư', 'u');
    SET @result = REPLACE(@result, N'ừ', 'u');
    SET @result = REPLACE(@result, N'ứ', 'u');
    SET @result = REPLACE(@result, N'ử', 'u');
    SET @result = REPLACE(@result, N'ữ', 'u');
    SET @result = REPLACE(@result, N'ự', 'u');
    SET @result = REPLACE(@result, N'ỳ', 'y');
    SET @result = REPLACE(@result, N'ý', 'y');
    SET @result = REPLACE(@result, N'ỷ', 'y');
    SET @result = REPLACE(@result, N'ỹ', 'y');
    SET @result = REPLACE(@result, N'ỵ', 'y');
    SET @result = REPLACE(@result, N'kh', 'k h');
    SET @result = REPLACE(@result, N'gi', 'g i');
    SET @result = REPLACE(@result, N'ng', 'n g');
    SET @result = REPLACE(@result, N'nh', 'n h');
    SET @result = REPLACE(@result, N'ph', 'p h');
    SET @result = REPLACE(@result, N'th', 't h');
    SET @result = REPLACE(@result, N'ch', 'c h');
    SET @result = REPLACE(@result, N'tr', 't r');
    SET @result = REPLACE(@result, N'gh', 'g h');
    SET @result = REPLACE(@result, N'qu', 'q u');
    RETURN @result;
END;
GO

-- =========================
-- 11. DỌN DẸP BẢNG TẠM
-- =========================

DROP TABLE #TempUsers;
DROP TABLE #TempPremiumUsers;

-- =========================
-- 12. KIỂM TRA DỮ LIỆU
-- =========================

-- Kiểm tra số lượng bản ghi
SELECT 'Users' as TableName, COUNT(*) as RecordCount FROM Users
UNION ALL
SELECT 'Courses', COUNT(*) FROM Courses
UNION ALL
SELECT 'Lessons', COUNT(*) FROM Lessons
UNION ALL
SELECT 'Enrollments', COUNT(*) FROM Enrollment
UNION ALL
SELECT 'Feedbacks', COUNT(*) FROM Feedbacks
UNION ALL
SELECT 'UserPremium', COUNT(*) FROM UserPremium
UNION ALL
SELECT 'Payments', COUNT(*) FROM Payments;

-- Kiểm tra rating trung bình
SELECT 
    c.Title,
    AVG(CAST(f.Rating AS FLOAT)) as AverageRating,
    COUNT(f.Rating) as RatingCount
FROM Courses c
LEFT JOIN Feedbacks f ON c.CourseID = f.CourseID
GROUP BY c.CourseID, c.Title;