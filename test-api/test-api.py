@app.get("/reference/good")
async def reference_root():
    return {"message": "Hello World"}


@app.get("/sample/good")
async def sample_root():
    return {"message": "Hello World"}

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8100)