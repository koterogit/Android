from ftplib import FTP

ftp = FTP()
ftp.host='localhost'
ftp.port = 2121
ftp.login('user','12345')
ftp.cwd('LOG')
ftp.retrlines('LIST')
with open('README', 'wb') as fp:
    ftp.retrbinary('RETR README',fp.write)
ftp.quit()
