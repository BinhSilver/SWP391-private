-- Trong phiên bản này Huy đã thay đổi thêm một hàm để format ký tự ghép và dấu với bảng liên quan đến chat messenger conversation 
CREATE DATABASE Wasabii;
GO
USE Wasabii;
GO

-- 1. Roles
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY,
    RoleName NVARCHAR(50) UNIQUE NOT NULL
);
INSERT INTO Roles VALUES (1, 'Free'), (2, 'Premium'), (3, 'Teacher'), (4, 'Admin');

-- 2. Premium Plans
CREATE TABLE PremiumPlans (
    PlanID INT PRIMARY KEY IDENTITY,
    PlanName NVARCHAR(100),
    Price DECIMAL(10, 2),
    DurationInMonths INT,
    Description NVARCHAR(255)
);

-- 3. Users
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
    Gender NVARCHAR(10) CONSTRAINT DF_Users_Gender DEFAULT N'Khác'
);

-- 4. Payments
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    PlanID INT NOT NULL,
    Amount DECIMAL(10, 2),
    PaymentDate DATETIME DEFAULT GETDATE(),
    TransactionNo NVARCHAR(100),
    BankCode NVARCHAR(50),
    OrderInfo NVARCHAR(255),
    ResponseCode NVARCHAR(10),
    TransactionStatus NVARCHAR(20),
    OrderCode NVARCHAR(100),
    CheckoutUrl NVARCHAR(255),
    Status NVARCHAR(20),
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Payments_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_Payments_Plans FOREIGN KEY (PlanID) REFERENCES PremiumPlans(PlanID)
);

-- 5. UserPremium
CREATE TABLE UserPremium (
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    PlanID INT FOREIGN KEY REFERENCES PremiumPlans(PlanID),
    StartDate DATETIME,
    EndDate DATETIME,
    PRIMARY KEY (UserID, PlanID)
);

-- 6. Courses & Lessons
CREATE TABLE Courses (
    CourseID INT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(MAX),   
    IsHidden BIT DEFAULT 0,
    IsSuggested BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE Lessons (
    LessonID INT PRIMARY KEY IDENTITY,
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Title NVARCHAR(255),
    IsHidden BIT DEFAULT 0
);

-- 7. Enrollment
CREATE TABLE Enrollment (
    EnrollmentID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    EnrolledAt DATETIME DEFAULT GETDATE()
);

-- 8. Course Ratings
CREATE TABLE CourseRatings (
    RatingID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    RatedAt DATETIME DEFAULT GETDATE()
);

-- 9. Lesson Materials
CREATE TABLE LessonMaterials (
    MaterialID INT PRIMARY KEY IDENTITY(1,1),
    LessonID INT NOT NULL,
    MaterialType NVARCHAR(50) NOT NULL,   -- 'Từ vựng' | 'Kanji' | 'Ngữ pháp'
    FileType NVARCHAR(50) NOT NULL,       -- 'PDF' | 'Video'
    Title NVARCHAR(255),
    FilePath NVARCHAR(MAX),
    IsHidden BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 10. Vocabulary, Tags, VocabularyTags
CREATE TABLE Vocabulary (
    VocabID INT PRIMARY KEY IDENTITY,
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    Reading NVARCHAR(100),
    Example NVARCHAR(MAX)
);
ALTER TABLE Vocabulary
ADD LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID);
ALTER TABLE Vocabulary
ADD imagePath VARCHAR(255) DEFAULT NULL;

CREATE TABLE Tags (
    TagID INT PRIMARY KEY IDENTITY,
    TagName NVARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE VocabularyTags (
    VocabID INT FOREIGN KEY REFERENCES Vocabulary(VocabID),
    TagID INT FOREIGN KEY REFERENCES Tags(TagID),
    PRIMARY KEY (VocabID, TagID)
);

-- 11. User Vocabulary
CREATE TABLE UserVocabulary (
    UserVocabID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 12. Kanji
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

-- 13. Grammar Points
CREATE TABLE GrammarPoints (
    GrammarID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255),
    Explanation NVARCHAR(MAX)
);

-- 14. Flashcards
CREATE TABLE Flashcards (
    FlashcardID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Title NVARCHAR(100)
);

CREATE TABLE FlashcardItems (
    FlashcardItemID INT PRIMARY KEY IDENTITY,
    FlashcardID INT FOREIGN KEY REFERENCES Flashcards(FlashcardID),
    VocabID INT NULL FOREIGN KEY REFERENCES Vocabulary(VocabID),
    UserVocabID INT NULL FOREIGN KEY REFERENCES UserVocabulary(UserVocabID),
    Note NVARCHAR(255)
);

-- 15. Quiz & Test (Updated)
CREATE TABLE Quizzes (
    QuizID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255)
);

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
    IsCorrect BIT
);

-- 16. Quiz & Test Results
CREATE TABLE QuizResults (
    ResultID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    QuizID INT FOREIGN KEY REFERENCES Quizzes(QuizID),
    Score INT,
    TakenAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE TestResults (
    ResultID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    TestID INT FOREIGN KEY REFERENCES Tests(TestID),
    Score INT,
    TakenAt DATETIME DEFAULT GETDATE()
);

-- 17. Feedback
CREATE TABLE Feedbacks (
    FeedbackID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Content NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE FeedbackVotes (
    VoteID INT PRIMARY KEY IDENTITY,
    FeedbackID INT FOREIGN KEY REFERENCES Feedbacks(FeedbackID),
    UserID INT FOREIGN KEY REFERENCES Users(UserID)
);

-- 18. Chat & Video Call
-- Bảng Conversations
CREATE TABLE Conversations (
    ConversationID INT PRIMARY KEY IDENTITY(1,1),
    User1ID INT NOT NULL,
    User2ID INT NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (User1ID) REFERENCES Users(UserID),
    FOREIGN KEY (User2ID) REFERENCES Users(UserID)
);

-- Bảng Messages
CREATE TABLE Messages (
    MessageID INT PRIMARY KEY IDENTITY(1,1),
    ConversationID INT NOT NULL,
    SenderID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    Type NVARCHAR(50) NOT NULL, -- Chỉ hỗ trợ 'text' trong phiên bản này
    IsRead BIT DEFAULT 0,
    IsRecall BIT DEFAULT 0,
    SentAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ConversationID) REFERENCES Conversations(ConversationID),
    FOREIGN KEY (SenderID) REFERENCES Users(UserID)
);

-- Bảng Blocks
CREATE TABLE Blocks (
    BlockerID INT NOT NULL,
    BlockedID INT NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (BlockerID, BlockedID),
    FOREIGN KEY (BlockerID) REFERENCES Users(UserID),
    FOREIGN KEY (BlockedID) REFERENCES Users(UserID)
);

CREATE TABLE VideoCalls (
    CallID INT PRIMARY KEY IDENTITY,
    CallerID INT FOREIGN KEY REFERENCES Users(UserID),
    ReceiverID INT FOREIGN KEY REFERENCES Users(UserID),
    CallStart DATETIME,
    CallEnd DATETIME
);

-- 19. Progress
CREATE TABLE Progress (
    ProgressID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    CompletionPercent INT CHECK (CompletionPercent BETWEEN 0 AND 100),
    LastAccessed DATETIME DEFAULT GETDATE()
);

-- 20. Rooms & Participants
CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY IDENTITY(1,1),
    HostUserID INT NOT NULL,
    LanguageLevel NVARCHAR(50) NOT NULL,
    GenderPreference NVARCHAR(20) NOT NULL DEFAULT N'Không yêu cầu',
    MinAge INT CHECK (MinAge >= 0),
    MaxAge INT,
    AllowApproval BIT DEFAULT 0,
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Rooms_Host FOREIGN KEY (HostUserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    CONSTRAINT CK_Rooms_GenderPreference CHECK (
        GenderPreference IN (N'Nam', N'Nữ', N'Không yêu cầu')
    )
);

CREATE TABLE RoomParticipants (
    RoomID INT NOT NULL,
    UserID INT NOT NULL,
    JoinedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT PK_RoomParticipants PRIMARY KEY (RoomID, UserID),
    CONSTRAINT FK_RoomParticipants_Room FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID) ON DELETE CASCADE,
    CONSTRAINT FK_RoomParticipants_User FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE NO ACTION
);

-- Seed data
INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description)
VALUES 
    (N'Gói Tháng', 25000, 1, N'Sử dụng Premium trong 1 tháng'),
    (N'Gói Năm', 250000, 12, N'Sử dụng Premium trong 12 tháng');

INSERT INTO Users (RoleID, Email, PasswordHash, GoogleID, FullName, BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Avatar, Gender)
VALUES (1, 'nguyenphamthanhbinh02@gmail.com', '123456789', NULL, N'Người Dùng', '2000-01-01', '0123456789', N'N5', N'Địa chỉ user', N'Việt Nam', NULL, N'Nam'),
       (4, 'huyphw2@gmail.com', '12345678', NULL, N'Huy Phan', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nam'),
	   (4, 'Admin@gmail.com', '123456789', NULL, N'TBinh', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nam');

-- Đảm bảo các khóa học có đều có CreatedAt (nếu migrate data)
UPDATE Courses SET CreatedAt = GETDATE() WHERE CreatedAt IS NULL;

GO
--thêm user này để coi được khóa học nha
INSERT INTO Users (RoleID, Email, PasswordHash, GoogleID, FullName, BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Avatar, Gender)
VALUES (3,'teacher@gmail.com', '123', NULL, 'Tanaka Sensei', '2004-07-04', '0911053612', 'N4', N'Địa chỉ teacher', N'Việt Nam', NULL, N'Nữ');

--Thêm mấy bảng này nha
CREATE TABLE LessonAccess (
    AccessID INT IDENTITY PRIMARY KEY,
    UserID INT NOT NULL,
    LessonID INT NOT NULL,
    AccessedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_LessonAccess_User FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_LessonAccess_Lesson FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID),
    CONSTRAINT UC_User_Lesson UNIQUE (UserID, LessonID)  -- tránh trùng lặp
);

CREATE TABLE LessonVocabulary (
    LessonID INT NOT NULL FOREIGN KEY REFERENCES Lessons(LessonID),
    VocabID INT NOT NULL FOREIGN KEY REFERENCES Vocabulary(VocabID),
    PRIMARY KEY (LessonID, VocabID)
);

ALTER TABLE Answers
ADD AnswerNumber INT CHECK (AnswerNumber BETWEEN 1 AND 4);

ALTER TABLE Lessons
ADD Description NVARCHAR(1000);

ALTER TABLE LessonMaterials
ADD CONSTRAINT FK_LessonMaterials_Lessons
FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID);

--Xóa dữ liệu khóa học cũ rồi thêm mới nhé ở dưới nha

DELETE FROM Answers;
DELETE FROM Questions;
DELETE FROM Quizzes;

DELETE FROM GrammarPoints;
DELETE FROM Kanji;
DELETE FROM Vocabulary;

DELETE FROM LessonMaterials;

-- Sau khi các bảng trên đã xóa, mới xóa Lesson và Course
DELETE FROM Lessons;
DELETE FROM Courses;

-- 1. Tạo khóa học mới N5
INSERT INTO Courses (Title, Description, IsHidden, IsSuggested)
VALUES (N'Khóa học Giao tiếp N5', N'Khóa học tiếng Nhật sơ cấp tập trung vào giao tiếp cơ bản.', 0, 1);

DECLARE @CourseID INT = SCOPE_IDENTITY();
-- 2. Thêm các bài học
INSERT INTO Lessons (CourseID, Title) VALUES
(@CourseID, N'Bài 1: Giới thiệu bản thân'),
(@CourseID, N'Bài 2: Hỏi thăm sức khỏe'),
(@CourseID, N'Bài 3: Hỏi đường đi');

-- Lấy ID từng bài học
DECLARE @Lesson1ID INT = (SELECT LessonID FROM Lessons WHERE CourseID = @CourseID AND Title = N'Bài 1: Giới thiệu bản thân');
DECLARE @Lesson2ID INT = (SELECT LessonID FROM Lessons WHERE CourseID = @CourseID AND Title = N'Bài 2: Hỏi thăm sức khỏe');
DECLARE @Lesson3ID INT = (SELECT LessonID FROM Lessons WHERE CourseID = @CourseID AND Title = N'Bài 3: Hỏi đường đi');

-- Bài 1
INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath)
VALUES 
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Từ vựng', N'PDF', N'Từ vựng Bài 1', N'files/courseN5/vocabN5/Lesson1.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Kanji', N'PDF', N'Kanji Bài 1', NULL),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 1', N'files/courseN5/grammarN5/Lesson1.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 1', N'files/lesson1_grammar.mp4');

-- Bài 2
INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath)
VALUES 
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Từ vựng', N'PDF', N'Từ vựng Bài 2', N'files/courseN5/vocabN5/Lesson2.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Kanji', N'PDF', N'Kanji Bài 2', NULL),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 2', N'files/courseN5/grammarN5/Lesson2.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 2', N'files/lesson2_grammar.mp4');

-- Bài 3
INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath)
VALUES 
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Từ vựng', N'PDF', N'Từ vựng Bài 3', N'files/courseN5/vocabN5/Lesson3.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Kanji', N'PDF', N'Kanji Bài 3', NULL),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 3', N'files/courseN5/grammarN5/Lesson3.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 3', N'files/lesson3_grammar.mp4');

-- xử lý dấu ký tự ghép ( đoạn này nhớ chạy riêng hàm này ) 
CREATE OR ALTER FUNCTION dbo.RemoveDiacritics(@input NVARCHAR(MAX))
RETURNS NVARCHAR(MAX)
AS
BEGIN
    DECLARE @result NVARCHAR(MAX) = LOWER(@input);

    -- Bỏ dấu thanh tiếng Việt
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

    -- Tách các ký tự ghép tiếng Việt
    SET @result = REPLACE(@result, N'kh', 'k h');
    SET @result = REPLACE(@result, N'gi', 'g i');
    SET @result = REPLACE(@result, N'ng', 'n g');
    SET @result = REPLACE(@result, N'nh', 'n h');
    SET @result = REPLACE(@result, N'ph', 'p h');
    SET @result = REPLACE(@result, N'th', 't h');
    SET @result = REPLACE(@result, N'ch', 'c h');
    SET @result = REPLACE(@result, N'tr', 't r');
    SET @result = REPLACE(@result, N'gh', 'g h');
    SET @result = REPLACE(@result, N'qu', 'q u'); -- Thêm qu

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


INSERT INTO [Wasabii_Huy].[dbo].[Vocabulary] 
([Word], [Meaning], [Reading], [Example], [LessonID], [imagePath])
VALUES
( N'水', N'nước', N'みず (mizu)', N'水を飲みます。', 1, N'/images/vocab/mizu.png'),
( N'食べる', N'ăn', N'たべる (taberu)', N'パンを食べます。', 1, N'/images/vocab/taberu.png'),
( N'猫', N'mèo', N'ねこ (neko)', N'猫が好きです。', 1, N'/images/vocab/neko.png'),
( N'先生', N'giáo viên', N'せんせい (sensei)', N'先生は教室にいます。', 1, N'/images/vocab/sensei.png'),
( N'行く', N'đi', N'いく (iku)', N'学校へ行きます。', 1, N'/images/vocab/iku.png'),
( N'見る', N'xem/nhìn', N'みる (miru)', N'映画を見ます。', 2, N'/images/vocab/miru.png'),
( N'本', N'sách', N'ほん (hon)', N'本を読みます。', 2, N'/images/vocab/hon.png'),
( N'車', N'xe ô tô', N'くるま (kuruma)', N'車を運転します。', 2, N'/images/vocab/kuruma.png'),
( N'天気', N'thời tiết', N'てんき (tenki)', N'今日の天気はいいです。', 2, N'/images/vocab/tenki.png'),
( N'友達', N'bạn bè', N'ともだち (tomodachi)', N'友達と話します。', 2, N'/images/vocab/tomodachi.png');

-- Cập nhật bảng Flashcards
ALTER TABLE Flashcards 
ADD 
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    IsPublic BIT DEFAULT 0,
    Description NVARCHAR(500),
    CoverImage NVARCHAR(500);


-- Cập nhật bảng FlashcardItems
ALTER TABLE FlashcardItems 
ADD 
    FrontContent NVARCHAR(500),
    BackContent NVARCHAR(500),
    FrontImage NVARCHAR(500),
    BackImage NVARCHAR(500),
    OrderIndex INT DEFAULT 0;


-- Thêm dữ liệu test cho flashcard
-- Đảm bảo có user với ID = 1 (hoặc thay đổi UserID phù hợp)

-- Thêm flashcard test
INSERT INTO Flashcards (UserID, Title, CreatedAt, UpdatedAt, IsPublic, Description, CoverImage)
VALUES 
(1, N'Flashcard Từ Vựng N5', GETDATE(), GETDATE(), 1, N'Bộ flashcard từ vựng N5 cơ bản', NULL),
(1, N'Flashcard Kanji N5', GETDATE(), GETDATE(), 0, N'Bộ flashcard kanji N5', NULL);

-- Lấy FlashcardID vừa tạo
DECLARE @FlashcardID1 INT = (SELECT FlashcardID FROM Flashcards WHERE Title = N'Flashcard Từ Vựng N5');
DECLARE @FlashcardID2 INT = (SELECT FlashcardID FROM Flashcards WHERE Title = N'Flashcard Kanji N5');

-- Thêm flashcard items cho flashcard 1
INSERT INTO FlashcardItems (FlashcardID, FrontContent, BackContent, Note, OrderIndex)
VALUES 
(@FlashcardID1, N'こんにちは', N'Xin chào', N'Chào hỏi cơ bản', 1),
(@FlashcardID1, N'ありがとう', N'Cảm ơn', N'Lời cảm ơn', 2),
(@FlashcardID1, N'さようなら', N'Tạm biệt', N'Lời chào tạm biệt', 3);

-- Thêm flashcard items cho flashcard 2
INSERT INTO FlashcardItems (FlashcardID, FrontContent, BackContent, Note, OrderIndex)
VALUES 
(@FlashcardID2, N'人', N'Người', N'Kanji chỉ người', 1),
(@FlashcardID2, N'水', N'Nước', N'Kanji chỉ nước', 2),
(@FlashcardID2, N'火', N'Lửa', N'Kanji chỉ lửa', 3); 

INSERT INTO [Wasabii].[dbo].[Vocabulary] (
    [Word],
    [Meaning],
    [Reading],
    [Example],
    [LessonID],
    [imagePath]
)
VALUES
(N'猫',        N'Con mèo',      N'ねこ',         N'私は猫が好きです。',              1, N'imgvocab\Doraemon_character.png'),
(N'学校',      N'Trường học',   N'がっこう',     N'彼は学校へ行きます。',            1, N'imgvocab\Doraemon_character.png'),
(N'食べる',    N'Ăn',           N'たべる',       N'私は寿司を食べたいです。',        1, N'imgvocab\Doraemon_character.png'),
(N'友達',      N'Bạn bè',       N'ともだち',     N'彼女は友達と遊びます。',          1, N'imgvocab\Doraemon_character.png'),
(N'先生',      N'Giáo viên',    N'せんせい',     N'先生は日本語を教えます。',        1, N'imgvocab\Doraemon_character.png');

  ALTER TABLE Users
ADD IsTeacherPending BIT DEFAULT 0,
    CertificatePath NVARCHAR(500);

      ALTER TABLE Courses ADD CreatedBy INT NULL;
ALTER TABLE Courses ADD CONSTRAINT FK_Courses_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID);




ALTER TABLE Courses
ADD imageUrl NVARCHAR(MAX) NULL;