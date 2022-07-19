package types

import (
	"fmt"
	"github.com/99designs/gqlgen/graphql"
	jsoniter "github.com/json-iterator/go"
	"io"
)

type Bytes jsoniter.Any

func MarshalBytes(b Bytes) graphql.Marshaler {
	return graphql.WriterFunc(func(w io.Writer) {
		io.WriteString(w, b.ToString())
	})

}

func UnmarshalBytes(v interface{}) (jsoniter.Any, error) {
	return nil, fmt.Errorf("%T is not []byte", v)
}
