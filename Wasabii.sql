-- =========================
-- 1. TẠO DATABASE VÀ SỬ DỤNG
-- =========================
IF DB_ID('Wasabii') IS NOT NULL
    DROP DATABASE Wasabii;
GO
CREATE DATABASE Wasabii;
GO
USE Wasabii;
GO

-- =========================
-- 2. TẠO BẢNG CHÍNH (ĐÚNG THỨ TỰ PHỤ THUỘC)
-- =========================
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY,
    RoleName NVARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE PremiumPlans (
    PlanID INT PRIMARY KEY IDENTITY,
    PlanName NVARCHAR(100),
    Price DECIMAL(10, 2),
    DurationInMonths INT,
    Description NVARCHAR(255)
);

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

-- Bảng Payments (chuẩn hóa, bổ sung các trường mới)
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

-- Bảng UserPremium: quản lý thời gian premium của user
CREATE TABLE UserPremium (
    UserID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    PlanID INT NOT NULL FOREIGN KEY REFERENCES PremiumPlans(PlanID),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    PRIMARY KEY (UserID, PlanID, StartDate)
);

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

CREATE TABLE Enrollment (
    EnrollmentID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    EnrolledAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE Lessons (
    LessonID INT PRIMARY KEY IDENTITY,
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Title NVARCHAR(255),
    Description NVARCHAR(1000),
    IsHidden BIT DEFAULT 0,
    OrderIndex INT DEFAULT 0
);

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

CREATE TABLE Vocabulary (
    VocabID INT PRIMARY KEY IDENTITY,
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    Reading NVARCHAR(100),
    Example NVARCHAR(MAX),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    imagePath VARCHAR(255) DEFAULT NULL
);

CREATE TABLE Tags (
    TagID INT PRIMARY KEY IDENTITY,
    TagName NVARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE VocabularyTags (
    VocabID INT FOREIGN KEY REFERENCES Vocabulary(VocabID),
    TagID INT FOREIGN KEY REFERENCES Tags(TagID),
    PRIMARY KEY (VocabID, TagID)
);

CREATE TABLE UserVocabulary (
    UserVocabID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE Kanji (
    KanjiID INT PRIMARY KEY IDENTITY,
    Character NVARCHAR(10),
    Onyomi NVARCHAR(100),
    Kunyomi NVARCHAR(100),
    Meaning NVARCHAR(255),
    StrokeCount INT,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID)
);

CREATE TABLE VocabularyKanji (
    VocabID INT FOREIGN KEY REFERENCES Vocabulary(VocabID),
    KanjiID INT FOREIGN KEY REFERENCES Kanji(KanjiID),
    PRIMARY KEY (VocabID, KanjiID)
);

CREATE TABLE GrammarPoints (
    GrammarID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255),
    Explanation NVARCHAR(MAX)
);

CREATE TABLE Flashcards (
    FlashcardID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Title NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    IsPublic BIT DEFAULT 0,
    Description NVARCHAR(500),
    CoverImage NVARCHAR(500)
);

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



CREATE TABLE Quizzes (
    QuizID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255)
);

-- Bảng Test tổng hợp
CREATE TABLE Tests (
    TestID INT PRIMARY KEY IDENTITY,
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Title NVARCHAR(255)
);

CREATE TABLE Questions (
    QuestionID INT PRIMARY KEY IDENTITY,
    QuizID INT NULL FOREIGN KEY REFERENCES Quizzes(QuizID),
    TestID INT NULL FOREIGN KEY REFERENCES Tests(TestID),
    QuestionText NVARCHAR(MAX),
    TimeLimit INT DEFAULT 30
);

CREATE TABLE Answers (
    AnswerID INT PRIMARY KEY IDENTITY,
    QuestionID INT FOREIGN KEY REFERENCES Questions(QuestionID),
    AnswerText NVARCHAR(MAX),
    IsCorrect BIT,
	AnswerNumber INT CHECK (AnswerNumber BETWEEN 1 AND 4)

);

CREATE TABLE QuizResults (
    ResultID INT PRIMARY KEY IDENTITY,             -- Tự tăng
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    QuizID INT FOREIGN KEY REFERENCES Quizzes(QuizID),
    Score INT,
    TakenAt DATETIME DEFAULT GETDATE()
);

-- Quản lý phòng học, chat, gọi video
CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY IDENTITY,
    HostUserID INT FOREIGN KEY REFERENCES Users(UserID) ON DELETE CASCADE,
    LanguageLevel NVARCHAR(50) NOT NULL,
    GenderPreference NVARCHAR(20) DEFAULT N'Không yêu cầu',
    MinAge INT CHECK (MinAge >= 0),
    MaxAge INT,
    AllowApproval BIT DEFAULT 0,
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT CK_Rooms_GenderPreference CHECK (
        GenderPreference IN (N'Nam', N'Nữ', N'Không yêu cầu')
    )
);

CREATE TABLE RoomParticipants (
    RoomID INT,
    UserID INT,
    JoinedAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (RoomID, UserID),
    FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE NO ACTION
);

CREATE TABLE Conversations (
    ConversationID INT PRIMARY KEY IDENTITY(1,1),
    User1ID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    User2ID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    CreatedAt DATETIME DEFAULT GETDATE()
);

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

CREATE TABLE Blocks (
    BlockerID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    BlockedID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    CreatedAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (BlockerID, BlockedID)
);

CREATE TABLE VideoCalls (
    CallID INT PRIMARY KEY IDENTITY,
    CallerID INT FOREIGN KEY REFERENCES Users(UserID),
    ReceiverID INT FOREIGN KEY REFERENCES Users(UserID),
    CallStart DATETIME,
    CallEnd DATETIME
);

-- Các bảng phụ trợ khác
CREATE TABLE LessonAccess (
    AccessID INT IDENTITY PRIMARY KEY,
    UserID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    LessonID INT NOT NULL FOREIGN KEY REFERENCES Lessons(LessonID),
    AccessedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT UC_User_Lesson UNIQUE (UserID, LessonID)
);

CREATE TABLE LessonVocabulary (
    LessonID INT NOT NULL FOREIGN KEY REFERENCES Lessons(LessonID),
    VocabID INT NOT NULL FOREIGN KEY REFERENCES Vocabulary(VocabID),
    PRIMARY KEY (LessonID, VocabID)
);

CREATE TABLE Feedbacks (
    FeedbackID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Content NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
	Rating INT CHECK (Rating BETWEEN 1 AND 5) NOT NULL DEFAULT 5
);

CREATE TABLE FeedbackVotes (
    VoteID INT PRIMARY KEY IDENTITY,
    FeedbackID INT FOREIGN KEY REFERENCES Feedbacks(FeedbackID),
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
	VoteType INT CHECK (VoteType IN (1, -1)) -- 1: like, -1: dislike
);

CREATE TABLE CourseRatings (
    RatingID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    RatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE Progress (
    ProgressID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    CompletionPercent INT CHECK (CompletionPercent BETWEEN 0 AND 100),
    LastAccessed DATETIME DEFAULT GETDATE()
);


-- =========================
-- 3. SEED DATA
-- =========================
INSERT INTO Roles VALUES (1, 'Free'), (2, 'Premium'), (3, 'Teacher'), (4, 'Admin');

INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description)
VALUES (N'Gói Tháng', 25000, 1, N'Sử dụng Premium trong 1 tháng'),
       (N'Gói Năm', 250000, 12, N'Sử dụng Premium trong 12 tháng');

-- Thêm users
INSERT INTO Users (RoleID, Email, PasswordHash, GoogleID, FullName, BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Avatar, Gender)
VALUES (1, 'nguyenphamthanhbinh02@gmail.com', '123456789', NULL, N'Người Dùng', '2000-01-01', '0123456789', N'N5', N'Địa chỉ user', N'Việt Nam', NULL, N'Nam'),
       (4, 'huyphw2@gmail.com', '12345678', NULL, N'Huy Phan', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nam'),
       (4, 'admin@gmail.com', '123456789', NULL, N'TBinh', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nam'),
       (3, 'teacher@gmail.com', '123', NULL, N'Tanaka Sensei', '2004-07-04', '0911053612', 'N4', N'Địa chỉ teacher', N'Việt Nam', NULL, N'Nữ');

-- Lấy UserID của giáo viên
DECLARE @TeacherID INT = (SELECT UserID FROM Users WHERE Email = 'teacher@gmail.com');

-- Tạo khóa học với CreatedBy là giáo viên
INSERT INTO Courses (Title, Description, IsHidden, IsSuggested, CreatedBy, imageUrl)
VALUES (N'Khóa học Giao tiếp N5', N'Khóa học tiếng Nhật sơ cấp tập trung vào giao tiếp cơ bản.', 0, 1, @TeacherID, N'image/N5thumbnail.jpg');
DECLARE @CourseID INT = SCOPE_IDENTITY();

INSERT INTO Lessons (CourseID, Title, Description, IsHidden)
VALUES (@CourseID, N'Bài 1: Giới thiệu bản thân', N'Nội dung bài 1', 0),
       (@CourseID, N'Bài 2: Hỏi thăm sức khỏe', N'Nội dung bài 2', 0),
       (@CourseID, N'Bài 3: Hỏi đường đi', N'Nội dung bài 3', 0);
DECLARE @LessonID1 INT = (SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân');
DECLARE @LessonID2 INT = (SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe');
DECLARE @LessonID3 INT = (SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi');

INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath, IsHidden)
VALUES (@LessonID1, N'Từ vựng', N'PDF', N'Từ vựng Bài 1', N'files/courseN5/vocabN5/Lesson1.pdf', 0),
       (@LessonID1, N'Kanji', N'PDF', N'Kanji Bài 1', N'files/courseN5/kanjiN5/Lesson8.pdf', 0),
       (@LessonID1, N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 1', N'files/courseN5/grammarN5/Lesson1.pdf', 0),
       (@LessonID1, N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 1', N'web/files/lesson27_grammarVideo_1752087643271', 0),
       (@LessonID2, N'Từ vựng', N'PDF', N'Từ vựng Bài 2', N'files/courseN5/vocabN5/Lesson2.pdf', 0),
       (@LessonID2, N'Kanji', N'PDF', N'Kanji Bài 2', N'files/courseN5/kanjiN5/Lesson2.pdf', 0),
       (@LessonID2, N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 2', N'files/courseN5/grammarN5/Lesson2.pdf', 0),
       (@LessonID2, N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 2', N'web/files/lesson27_grammarVideo_1752087643271', 0),
       (@LessonID3, N'Từ vựng', N'PDF', N'Từ vựng Bài 3', N'files/courseN5/vocabN5/Lesson3.pdf', 0),
       (@LessonID3, N'Kanji', N'PDF', N'Kanji Bài 3', N'files/courseN5/kanjiN5/Lesson3.pdf', 0),
       (@LessonID3, N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 3', N'files/courseN5/grammarN5/Lesson3.pdf', 0),
       (@LessonID3, N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 3', N'web/files/lesson27_grammarVideo_1752087643271', 0);

INSERT INTO Vocabulary ([Word], [Meaning], [Reading], [Example], [LessonID], [imagePath])
VALUES (N'水', N'nước', N'みず (mizu)', N'水を飲みます。', @LessonID1, N'mizu.png'),
       (N'食べる', N'ăn', N'たべる (taberu)', N'パンを食べます。', @LessonID1, N'taberu.png'),
       (N'学生', N'học sinh, sinh viên', N'がくせい (gakusei)', N'私は学生です。', @LessonID1, N'gakusei.jpeg');

INSERT INTO Quizzes (LessonID, Title)
VALUES (@LessonID1, N'Quiz Bài 1');
DECLARE @QuizID INT = SCOPE_IDENTITY();

INSERT INTO Questions (QuizID, QuestionText, TimeLimit)
VALUES (@QuizID, N'Từ 「水」 có nghĩa là gì?', 30),
       (@QuizID, N'Từ 「食べる」 đọc là gì?', 30);
DECLARE @Q1 INT = (SELECT QuestionID FROM Questions WHERE QuestionText = N'Từ 「水」 có nghĩa là gì?');
DECLARE @Q2 INT = (SELECT QuestionID FROM Questions WHERE QuestionText = N'Từ 「食べる」 đọc là gì?');

INSERT INTO Answers (QuestionID, AnswerText, IsCorrect, AnswerNumber)
VALUES (@Q1, N'nước', 1, 1),
       (@Q1, N'lửa', 0, 2),
       (@Q1, N'cơm', 0, 3),
       (@Q1, N'cá', 0, 4),
       (@Q2, N'たべる (taberu)', 1, 1),
       (@Q2, N'のむ (nomu)', 0, 2),
       (@Q2, N'いく (iku)', 0, 3),
       (@Q2, N'みる (miru)', 0, 4);

INSERT INTO QuizResults (UserID, QuizID, Score)
VALUES (1, @QuizID, 100);


INSERT INTO Enrollment (UserID, CourseID)
VALUES (1, @CourseID);

-- =========================
-- 4. HÀM XỬ LÝ DẤU TIẾNG VIỆT
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

-- Đoạn này thử chạy hết phần dưới về chạy lẻ nó thiếu declare
-- 1. Thêm 50 người dùng
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

-- 2. Thêm 30 bản ghi Enrollment
DECLARE @EnrollmentCount INT = 30;
SET @Counter = 1;
DECLARE @UserID INT;
DECLARE @CourseID INT = (SELECT CourseID FROM Courses WHERE Title = N'Khóa học Giao tiếp N5');
DECLARE @EnrolledAt DATETIME;

-- Cursor để lấy ngẫu nhiên 30 người dùng từ bảng tạm
DECLARE user_cursor CURSOR FOR 
SELECT TOP 30 UserID, CreatedAt 
FROM #TempUsers 
ORDER BY NEWID(); -- Randomize

OPEN user_cursor;
FETCH NEXT FROM user_cursor INTO @UserID, @CreatedAt;

WHILE @Counter <= @EnrollmentCount
BEGIN
    -- Tính EnrolledAt trải đều từ 01/01/2025 đến 06/22/2025
    SET @EnrolledAt = DATEADD(DAY, (@DaysDiff * (@Counter - 1)) / @EnrollmentCount, @StartDate);

    -- Thêm bản ghi Enrollment
    INSERT INTO Enrollment (UserID, CourseID, EnrolledAt)
    VALUES (@UserID, @CourseID, @EnrolledAt);

    FETCH NEXT FROM user_cursor INTO @UserID, @CreatedAt;
    SET @Counter = @Counter + 1;
END;

CLOSE user_cursor;
DEALLOCATE user_cursor;

-- Xóa bảng tạm
DROP TABLE #TempUsers;
